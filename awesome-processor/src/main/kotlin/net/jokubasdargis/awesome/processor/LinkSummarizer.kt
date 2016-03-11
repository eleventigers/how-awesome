package net.jokubasdargis.awesome.processor

interface LinkSummarizer {
    fun summarize(link: Link) : LinkSummary
}