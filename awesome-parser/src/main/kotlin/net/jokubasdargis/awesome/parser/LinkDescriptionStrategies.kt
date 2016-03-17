package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDescription
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.text.Normalizer

internal class LinkDescriptionStrategies {
    companion object {
        private val NONE_STRATEGY: (Link) -> (Element) -> LinkDescription = { link ->
            {
                LinkDescription.None(link)
            }
        }

        private val TITLE_STRATEGY: (Link) -> (Element) -> LinkDescription = { link ->
            {
                var title = it.text()
                if (title.isNullOrBlank()) {
                    title = findTextInSiblings(it)
                }

                title = clean(title)
                if (title != null) {
                    LinkDescription.Title(link, title)
                } else {
                    LinkDescription.None(link)
                }
            }
        }

        private val SUMMARY_STRATEGY: (Link) -> (Element) -> LinkDescription = { link ->
            {
                var summary = clean(findTextInSiblings(it))
                if (summary != null) {
                    LinkDescription.Summary(link, summary)
                } else {
                    LinkDescription.None(link)
                }
            }
        }

        private fun findTextInSiblings(element: Element) : String? {
            var text: String? = null

            val siblingNode = element.nextSibling()
            if (siblingNode is TextNode) {
                text = siblingNode.text()
            }

            if (text.isNullOrBlank()) {
                val siblingElement = element.nextElementSibling();
                if (!Html.Tag.lists().contains(Html.Tag.from(siblingElement?.tagName()))) {
                    text = siblingElement?.text()
                }
            }

            return text
        }

        private val LETTERS_DIGITS = "^[^a-zA-z0-9()]*|[^a-zA-Z0-9()]*$".toRegex()

        private fun clean(value : String?) : String? {
            if (!value.isNullOrBlank()) {
                val normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
                val cleaned = normalized.replace(LETTERS_DIGITS, "")
                return if (cleaned.isBlank()) null else cleaned
            } else {
                return null
            }
        }

        fun title() : (Link) -> (Element) -> LinkDescription {
            return TITLE_STRATEGY
        }


        fun summary() : (Link) -> (Element) -> LinkDescription {
            return SUMMARY_STRATEGY
        }

        fun none() : (Link) -> (Element) -> LinkDescription {
            return NONE_STRATEGY
        }
    }
}
