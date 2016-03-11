package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Element

internal class LinkFinder(internal val elements: List<Element> = emptyList(),
                          internal val root: Link) {

    fun find() : LinkList {
        val pairs = elements
                .map { Pair(it, Link.from(Html.href(it), root)) }
                .map {
                    val that = it
                    val el = that.first
                    val allLinks = hashSetOf<Element>()
                            .plus(nearbyParagraphLinks(el))
                            .plus(nearbyListLinks(el))
                            .map { Link.from(Html.href(it), that.second) }

                    Pair(it.second, allLinks)
                }
                .groupBy { p -> p.first.uri }
                .map { g ->
                    // prefer link with greater relations
                    g.value.fold(g.value.first(), { a, p ->
                        if (p.second.size < a.second.size ) a else p
                    })
                }

        return LinkList.from(pairs)
    }

    private fun nearbyListLinks(el: Element) : List<Element> {
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

    private fun nearbyParagraphLinks(el: Element) : List<Element> {
        val parent = el.parent()
        return Html.closestByTag(
                parent,
                Html.Tag.P,
                Functions.and(
                        { n -> n.allElements.filter {
                            Html.Tag.headings().contains(Html.Tag.from(it.tagName())) }.isEmpty()
                        }
                ))
                ?.getElementsByAttribute(Html.Attr.HREF.value)
                ?.filter { it -> it.parent() != parent }
                ?: emptyList()
    }
}