package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.util.Functions
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.Locale

internal class Html {

    internal enum class Attr(val value: String) {
        HREF("href"),
        REL("rel"),
        PROPERTY("property"),
        CONTENT("content"),
        ITEMPROP("itemprop"),
        DATETIME("datetime");
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
        P("p"),
        META("meta"),
        TIME("time");

        fun apply(value: String?) : Boolean {
            return this.value.equals(value?.toLowerCase(Locale.ENGLISH))
        }

        companion object {
            private val HEADINGS = setOf(H1, H2, H3, H4, H5, H6)
            private val LISTS = setOf(UL, LI)

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

            fun lists() : Set<Tag> {
                return LISTS
            }
        }
    }

    internal enum class Dir {
        UP,
        DOWN
    }

    companion object {

        private val DEFAULT_ATTR_ELEMENTS: (Element, Attr) -> () -> List<Element> = { el, attr ->
            {
                el.getElementsByAttribute(attr.value) ?: emptyList<Element>()
            }
        }

        fun byAttr(el: Element, attr: Attr) : () -> List<Element> {
            return DEFAULT_ATTR_ELEMENTS(el, attr)
        }

        fun links(el: Element) : () -> List<Element> {
            return byAttr(el, Attr.HREF)
        }

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
