package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

internal class LinkFinder(internal val elements: List<Element> = emptyList(),
                          internal val root: Link) {

    fun find() : LinkList {
        val pairs = elements
                .map { Pair(it, Link.from(href(it), root)) }
                .map {
                    val that = it
                    val el = that.first
                    val allLinks = hashSetOf<Element>()
                            .plus(nearbyParagraphLinks(el))
                            .plus(nearbyListLinks(el))
                            .map { Link.from(href(it), that.second) }

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

    private fun href(node: Node) : String {
        return node.attr(HtmlAttr.HREF.value).trim()
    }

    private fun nearbyListLinks(el: Element) : List<Element> {
        val parent = el.parent()
        return closestByTag(
                parent,
                HtmlTag.UL,
                Functions.and(
                        { n -> !HtmlTag.headings().contains(HtmlTag.from(n.tagName())) },
                        { n -> !HtmlTag.LI.apply(n.tagName()) }
                ))
                ?.getElementsByAttribute(HtmlAttr.HREF.value)
                ?: emptyList()
    }

    private fun nearbyParagraphLinks(el: Element) : List<Element> {
        val parent = el.parent()
        return closestByTag(
                parent,
                HtmlTag.P,
                Functions.and(
                        { n -> n.allElements.filter {
                            HtmlTag.headings().contains(HtmlTag.from(it.tagName())) }.isEmpty()
                        }
                ))
                ?.getElementsByAttribute(HtmlAttr.HREF.value)
                ?.filter { it -> it.parent() != parent }
                ?: emptyList()
    }

    private fun closestByTag(el: Element, tag: HtmlTag,
                             traverseNext: (Element) -> Boolean = Functions.always(true),
                             dir: Dir = Dir.DOWN): Element? {
        var tagElem : Element? = el.getElementsByTag(tag.value).first()
        if (tagElem != null) {
            return tagElem
        }
        val next = if (Dir.DOWN.equals(dir))
            el.nextElementSibling() else el.previousElementSibling()
        if (next != null && traverseNext(next)) {
            tagElem = closestByTag(next, tag, traverseNext, dir);
            if (tagElem != null) {
                return tagElem
            }
        }
        return null
    }

    internal enum class Dir {
        UP,
        DOWN
    }

    internal enum class HtmlAttr(val value: String) {
        HREF("href");
    }

    internal enum class HtmlTag(val value: String) {
        UL("ul"),
        LI("li"),
        H1("h1"),
        H2("h2"),
        H3("h3"),
        H4("h4"),
        H5("h5"),
        H6("h6"),
        P("p");

        fun apply(value: String?) : Boolean {
            return this.value.equals(value)
        }

        companion object {
            val HEADINGS = setOf(H1, H2, H3, H4, H5, H6)

            fun from(value: String?) : HtmlTag? {
                values().forEach {
                    if (it.value.equals(value)) {
                        return it
                    }
                }
                return null;
            }

            fun headings() : Set<HtmlTag> {
                return HEADINGS
            }
        }
    }
}