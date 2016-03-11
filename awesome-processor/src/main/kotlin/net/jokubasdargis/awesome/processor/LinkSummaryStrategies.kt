package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.text.Normalizer

internal class LinkSummaryStrategies {
    companion object {
        private val NOOP_SUMMARY_STRATEGY : (Link) -> (Element) -> LinkSummary = { link ->
            {
                LinkSummary(link)
            }
        }

        private val BASIC_SUMMARY_STRATEGY : (Link) -> (Element) -> LinkSummary = { link ->
            {
                var title = it.text()
                if (title.isNullOrBlank()) {
                    title = findTextInSiblings(it)
                }

                val desc = findTextInSiblings(it)

                LinkSummary(link, clean(title), clean(desc))
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

        fun default() : (Link) -> (Element) -> LinkSummary {
            return BASIC_SUMMARY_STRATEGY
        }

        fun noop() : (Link) -> (Element) -> LinkSummary {
            return NOOP_SUMMARY_STRATEGY
        }
    }
}