package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DefaultLinkDescriberTest : BaseDocumentTest() {

    @Test
    fun describeKotlin() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title).isEqualTo("Kotlin")
            }
        }
    }

    @Test
    fun describeUnity() {
        val link = Link.from("https://github.com/RyanNielson/awesome-unity")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title).isEqualTo("Unity")
                is LinkDescription.Summary -> assertThat(it.summary).isEqualTo("(Game engine)")
            }
        }
    }

    @Test
    fun describeKtor() {
        val link = Link.from("https://github.com/Kotlin/ktor")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome-kotlin.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title).isEqualTo("Kotlin/ktor")
                is LinkDescription.Summary -> assertThat(it.summary)
                        .isEqualTo("Web backend framework for Kotlin")
            }
        }
    }

    @Test
    fun describeKotlinDaggerExample() {
        val link = Link.from("https://github.com/damianpetla/kotlin-dagger-example")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome-kotlin.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title)
                        .isEqualTo("damianpetla/kotlin-dagger-example")
                is LinkDescription.Summary -> assertThat(it.summary)
                        .isEqualTo("Example of Android project showing integration with Kotlin and Dagger 2")
            }
        }
    }

    @Test
    fun describeBrikk() {
        val link = Link.from("https://github.com/brikk/brikk")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome-kotlin.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title).isEqualTo("brikk/brikk")
                is LinkDescription.Summary -> assertThat(it.summary)
                        .isEqualTo("Brikk dependency manager (Kotlin, KotlinJS, Java, …​)")
            }
        }
    }

    @Test
    fun describeOkHttp() {
        val link = Link.from("http://square.github.io/okhttp")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome-java.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title).isEqualTo("OkHttp")
                is LinkDescription.Summary -> assertThat(it.summary).isEqualTo("HTTP+SPDY client")
            }
        }
    }

    @Test
    fun describeSwift() {
        val link = Link.from("https://github.com/matteocrippa/awesome-swift")
        val describer = DefaultLinkDescriber.create(readmeDocument("awesome.html")!!)
        val descriptions = describer.describe(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it.title).isEqualTo("Swift")
            }
        }
    }

    @Test
    fun noneSummaryStrategy() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val describer = DefaultLinkDescriber.create(
                readmeDocument("awesome.html")!!, listOf(LinkDescriptionStrategies.none()))
        val descriptions = describer.describe(link)

        assertThat(descriptions[0]).isEqualTo(LinkDescription.None())
    }

    @Test
    fun describePlentyAwesome() {
        val document = readmeDocument("awesome.html");
        val linkFinder = DefaultLinkFinder.create(document!!)
        val linkList = linkFinder.find(Link.from("https://github.com/sindresorhus/awesome"))
        val describer = DefaultLinkDescriber.create(document)

        linkList.links().forEach {
            val descriptions = describer.describe(it)
            descriptions.forEach {
                when (it) {
                    is LinkDescription.Title -> assertThat(it.title).isNotEmpty()
                    is LinkDescription.Summary -> assertThat(it.summary).isNotEmpty()
                }
            }
        }
    }

    @Test
    fun describePlentyKotlin() {
        val document = readmeDocument("awesome-kotlin.html");
        val linkFinder = DefaultLinkFinder.create(document!!)
        val linkList = linkFinder.find(Link.from("https://github.com/JavaBy/awesome-kotlin"))
        val describer = DefaultLinkDescriber.create(document)

        linkList.links().forEach {
            val descriptions = describer.describe(it)
            descriptions.forEach {
                when (it) {
                    is LinkDescription.Title -> assertThat(it.title).isNotEmpty()
                    is LinkDescription.Summary -> assertThat(it.summary).isNotEmpty()
                }
            }
        }
    }
}
