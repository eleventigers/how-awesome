package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal class DefaultLinkFinder private constructor(private val document: Document) : LinkFinder {

    override fun find(rootLink: Link): LinkList {
        val elements: List<Element> = document
                .getElementsByAttribute(Html.Attr.HREF.value) ?: emptyList<Element>()
        val pairs = elements
                .map { Pair(it, Link.from(Html.href(it), rootLink)) }
                .map {
                    val that = it
                    val el = that.first
                    val allLinks = hashSetOf<Element>()
                            .plus(nearbyParagraphLinks(el))
                            .plus(nearbyListLinks(el))
                            .map { Link.from(Html.href(it), that.second) }

                    Pair(it.second, allLinks)
                }
                .groupBy { p -> p.first.raw() }
                .map { g ->
                    // prefer link with greater relations
                    g.value.fold(g.value.first(), { a, p ->
                        if (p.second.size < a.second.size ) a else p
                    })
                }

        return LinkList.from(pairs)
    }

    private fun nearbyListLinks(el: Element): List<Element> {
        val parent = el.parent()
        return Html.closestByTag(
                parent,
                Html.Tag.UL,
                Functions.and(
                        { n -> !Html.Tag.headings().contains(Html.Tag.from(n.tagName())) },
                        { n -> !Html.Tag.LI.apply(n.tagName()) }
                ))
                ?.getElementsByAttribute(Html.Attr.HREF.value)
                ?: emptyList()
    }

    private fun nearbyParagraphLinks(el: Element): List<Element> {
        val parent = el.parent()
        return Html.closestByTag(
                parent,
                Html.Tag.P,
                Functions.and(
                        { n ->
                            n.allElements.filter {
                                Html.Tag.headings().contains(Html.Tag.from(it.tagName()))
                            }.isEmpty()
                        }
                ))
                ?.getElementsByAttribute(Html.Attr.HREF.value)
                ?.filter { it -> it.parent() != parent }
                ?: emptyList()
    }

    companion object {
        fun create(document: Document): LinkFinder {
            return DefaultLinkFinder(document)
        }
    }
}