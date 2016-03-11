package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

internal class Html {

    internal enum class Attr(val value: String) {
        HREF("href");
    }

    internal enum class Tag(val value: String) {
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

            fun from(value: String?) : Tag? {
                values().forEach {
                    if (it.value.equals(value)) {
                        return it
                    }
                }
                return null;
            }

            fun headings() : Set<Tag> {
                return HEADINGS
            }
        }
    }

    internal enum class Dir {
        UP,
        DOWN
    }

    companion object {
        fun href(node: Node) : String {
            return node.attr(Attr.HREF.value).trim()
        }

        fun closestByTag(el: Element, tag: Html.Tag,
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
    }
}
