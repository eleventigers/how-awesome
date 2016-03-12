package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal class DefaultLinkSummarizer private constructor(
        internal val strategy: (Link) -> (Element) -> LinkSummary) : LinkSummarizer {

    override fun summarize(link: Link, document: Document) : LinkSummary {
        val summaries = document
                .getElementsByAttributeValueContaining(
                        Html.Attr.HREF.value,
                        link.raw())
                .map(strategy(link))

        return summaries.first()
    }

    companion object {
        fun create(strategy: (Link) -> (Element) -> LinkSummary
                   = LinkSummaryStrategies.default()) : LinkSummarizer {
            return DefaultLinkSummarizer(strategy)
        }
    }
}