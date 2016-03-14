package net.jokubasdargis.awesome.processor

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class DefaultLinkSummarizerTest : BaseDocumentTest() {

    @Test
    fun summarizeKotlin() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("Kotlin")
        assertThat(summary.description).isNull()
    }

    @Test
    fun summarizeUnity() {
        val link = Link.from("https://github.com/RyanNielson/awesome-unity")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("Unity")
        assertThat(summary.description).isEqualTo("(Game engine)")
    }

    @Test
    fun summarizeKtor() {
        val link = Link.from("https://github.com/Kotlin/ktor")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome-kotlin.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("Kotlin/ktor")
        assertThat(summary.description).isEqualTo("Web backend framework for Kotlin")
    }

    @Test
    fun summarizeKotlinDaggerExample() {
        val link = Link.from("https://github.com/damianpetla/kotlin-dagger-example")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome-kotlin.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("damianpetla/kotlin-dagger-example")
        assertThat(summary.description).isEqualTo(
                "Example of Android project showing integration with Kotlin and Dagger 2")
    }

    @Test
    fun summarizeBrikk() {
        val link = Link.from("https://github.com/brikk/brikk")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome-kotlin.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("brikk/brikk")
        assertThat(summary.description).isEqualTo(
                "Brikk dependency manager (Kotlin, KotlinJS, Java, …​)")
    }

    @Test
    fun summarizeOkHttp() {
        val link = Link.from("http://square.github.io/okhttp")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome-java.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("OkHttp")
        assertThat(summary.description).isEqualTo("HTTP+SPDY client")
    }

    @Test
    fun summarizeSwift() {
        val link = Link.from("https://github.com/matteocrippa/awesome-swift")
        val summarizer = DefaultLinkSummarizer.create(readmeDocument("awesome.html")!!)
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isEqualTo("Swift")
        assertThat(summary.description).isNull()
    }

    @Test
    fun noopSummaryStrategy() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val summarizer = DefaultLinkSummarizer.create(
                readmeDocument("awesome.html")!!, LinkSummaryStrategies.noop())
        val summary = summarizer.summarize(link)

        assertThat(summary.title).isNull()
        assertThat(summary.description).isNull()
    }

    @Test
    fun summarizePlentyAwesome() {
        val document = readmeDocument("awesome.html");
        val linkFinder = DefaultLinkFinder.create(document!!)
        val linkList = linkFinder.find(Link.from("https://github.com/sindresorhus/awesome"))
        val summarizer = DefaultLinkSummarizer.create(document)

        linkList.links().forEach {
            val summary = summarizer.summarize(it)
            assertThat(summary.link).isEqualTo(it)
            if (summary.title != null) {
                assertThat(summary.title).isNotEmpty()
            }
            if (summary.description != null) {
                assertThat(summary.description).isNotEmpty()
            }
        }
    }

    @Test
    fun summarizePlentyKotlin() {
        val document = readmeDocument("awesome-kotlin.html");
        val linkFinder = DefaultLinkFinder.create(document!!)
        val linkList = linkFinder.find(Link.from("https://github.com/JavaBy/awesome-kotlin"))
        val summarizer = DefaultLinkSummarizer.create(document)

        linkList.links().forEach {
            val summary = summarizer.summarize(it)
            assertThat(summary.link).isEqualTo(it)
            if (summary.title != null) {
                assertThat(summary.title).isNotEmpty()
            }
            if (summary.description != null) {
                assertThat(summary.description).isNotEmpty()
            }
        }
    }
}
