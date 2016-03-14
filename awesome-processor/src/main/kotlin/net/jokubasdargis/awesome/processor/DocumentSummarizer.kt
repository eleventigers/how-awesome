package net.jokubasdargis.awesome.processor

interface DocumentSummarizer {
    fun summarize(documentLink: Link): DocumentSummary
}