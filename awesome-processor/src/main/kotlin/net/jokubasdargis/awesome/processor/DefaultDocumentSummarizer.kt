package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document

internal class DefaultDocumentSummarizer private constructor(
        private val linkFinder: LinkFinder,
        private val linkSummarizer: LinkSummarizer) : DocumentSummarizer {

    override fun summarize(documentLink: Link): DocumentSummary {
        val linkList = linkFinder.find(documentLink)
        val linkSummaries = linkList.links().map { linkSummarizer.summarize(it) }

        return DocumentSummary.create(
                documentLink,
                linkList.links(),
                linkSummaries,
                linkList.relationships())
    }

    companion object {
        fun create(document: Document): DefaultDocumentSummarizer {
            return DefaultDocumentSummarizer(
                    DefaultLinkFinder.create(document),
                    DefaultLinkSummarizer.create(document))
        }
    }
}