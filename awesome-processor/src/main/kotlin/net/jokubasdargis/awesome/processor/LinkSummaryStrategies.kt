package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.text.Normalizer

internal class LinkSummaryStrategies {
    companion object {
        private val BASIC_SUMMARY_STRATEGY: (Link) -> (Element) -> LinkSummary = { link ->
            {
                val title = it.text()
                var desc: String? = null

                val siblingNode = it.nextSibling()
                if (siblingNode is TextNode) {
                    desc = siblingNode.text()
                }

                if (desc.isNullOrBlank()) {
                    desc = it.nextElementSibling()?.text()
                }

                LinkSummary(link, clean(title), clean(desc))
            }
        }

        private val LETTERS_DIGITS = "^[^a-zA-z0-9()]*|[^a-zA-Z0-9()]*$".toRegex()

        private fun clean(value : String?) : String? {
            if (value != null) {
                val normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
                return normalized.replace(LETTERS_DIGITS, "").trim()
            } else {
                return value
            }
        }

        fun default() : (Link) -> (Element) -> LinkSummary {
            return BASIC_SUMMARY_STRATEGY
        }
    }
}