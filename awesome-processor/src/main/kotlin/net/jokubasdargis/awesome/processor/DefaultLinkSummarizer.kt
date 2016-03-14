package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal class DefaultLinkSummarizer private constructor(private val document: Document,
        private  val strategy: (Link) -> (Element) -> LinkSummary) : LinkSummarizer {

    override fun summarize(link: Link) : LinkSummary {
        val summaries = document
                .getElementsByAttributeValueContaining(
                        Html.Attr.HREF.value,
                        link.raw())
                .map(strategy(link))

        return summaries.first()
    }

    companion object {
        fun create(document: Document, strategy: (Link) -> (Element) -> LinkSummary
                   = LinkSummaryStrategies.default()) : LinkSummarizer {
            return DefaultLinkSummarizer(document, strategy)
        }
    }
}