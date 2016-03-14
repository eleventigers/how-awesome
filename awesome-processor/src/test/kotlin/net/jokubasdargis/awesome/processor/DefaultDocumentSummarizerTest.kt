package net.jokubasdargis.awesome.processor

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class DefaultDocumentSummarizerTest : BaseDocumentTest() {

    @Test
    fun summarize() {
        val summarizer = DefaultDocumentSummarizer.create(readmeDocument("awesome.html")!!)

        val summary = summarizer.summarize(Link.from("https://github.com/sindresorhus/awesome"))

        assertThat(summary.links()).hasSize(318)
        assertThat(summary.linksOfDocument()).hasSize(22)
        assertThat(summary.linksOfHost(Host.GITHUB)).hasSize(311)
        assertThat(summary.linksNotOfHost(Host.GITHUB)).hasSize(7)
    }
}