package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkRelationship
import net.jokubasdargis.awesome.core.Relationship
import net.jokubasdargis.awesome.util.Functions
import org.jsoup.nodes.Element

internal class DefaultLinkRelationshipFinder private constructor(
        private val linkElements: () -> List<Element>) : (Link) -> List<Relationship<Link>> {

    override fun invoke(value: Link): List<Relationship<Link>> {
        val pairs = linkElements()
                .map { Pair(it, Link.from(Html.href(it), value)) }
                .map {
                    val that = it
                    val el = that.first
                    val allLinks = hashSetOf<Element>()
                            .plus(nearbyParagraphLinks(el))
                            .plus(nearbyListLinks(el))
                            .map { Link.from(Html.href(it), that.second) }

                    Pair(it.second, allLinks)
                }
                .groupBy { p -> p.first.raw }
                .map { g ->
                    // prefer link with greater relations
                    g.value.fold(g.value.first(), { a, p ->
                        if (p.second.size < a.second.size ) a else p
                    })
                }

        val relationships = if (pairs.isEmpty()) emptyList() else pairs.map { p ->
            p.second.map {
                LinkRelationship(p.first, it)
            }
        }.reduce { a, l -> a.plus(l) }

        return relationships
    }

    private fun nearbyListLinks(el: Element): List<Element> {
        val parent: Element? = el.parent()
        return if (parent != null) Html.closestByTag(
                parent,
                Html.Tag.UL,
                Functions.and(
                        { n -> !Html.Tag.headings().contains(Html.Tag.from(n.tagName())) },
                        { n -> !Html.Tag.LI.apply(n.tagName()) }
                ))
                ?.getElementsByAttribute(Html.Attr.HREF.value)
                ?: emptyList() else emptyList()
    }

    private fun nearbyParagraphLinks(el: Element): List<Element> {
        val parent: Element? = el.parent()
        return if (parent != null) Html.closestByTag(
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
                ?: emptyList() else emptyList()
    }

    companion object {
        fun create(linkElements: () -> List<Element>): (Link) -> List<Relationship<Link>> {
            return DefaultLinkRelationshipFinder(linkElements)
        }
    }
}
