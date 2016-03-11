package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document

interface LinkSummarizer {
    fun summarize(link: Link, document: Document) : LinkSummary
}