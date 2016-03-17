package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDescription
import org.junit.Test

class DefaultLinkDescriberIntegrationTest : BaseIntegrationTest() {

    @Test fun describeKotlin() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it()).isEqualTo("Kotlin")
            }
        }
    }

    @Test fun describeUnity() {
        val link = Link.from("https://github.com/RyanNielson/awesome-unity")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it()).isEqualTo("Unity")
                is LinkDescription.Summary -> assertThat(it()).isEqualTo("(Game engine)")
            }
        }
    }

    @Test fun describeKtor() {
        val link = Link.from("https://github.com/Kotlin/ktor")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome-kotlin.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it()).isEqualTo("Kotlin/ktor")
                is LinkDescription.Summary -> assertThat(it())
                        .isEqualTo("Web backend framework for Kotlin")
            }
        }
    }

    @Test fun describeKotlinDaggerExample() {
        val link = Link.from("https://github.com/damianpetla/kotlin-dagger-example")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome-kotlin.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it())
                        .isEqualTo("damianpetla/kotlin-dagger-example")
                is LinkDescription.Summary -> assertThat(it()).isEqualTo(
                        "Example of Android project showing integration with Kotlin and Dagger 2")
            }
        }
    }

    @Test fun describeBrikk() {
        val link = Link.from("https://github.com/brikk/brikk")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome-kotlin.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it()).isEqualTo("brikk/brikk")
                is LinkDescription.Summary -> assertThat(it())
                        .isEqualTo("Brikk dependency manager (Kotlin, KotlinJS, Java, …​)")
            }
        }
    }

    @Test fun describeOkHttp() {
        val link = Link.from("http://square.github.io/okhttp")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome-java.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it()).isEqualTo("OkHttp")
                is LinkDescription.Summary -> assertThat(it()).isEqualTo("HTTP+SPDY client")
            }
        }
    }

    @Test fun describeSwift() {
        val link = Link.from("https://github.com/matteocrippa/awesome-swift")
        val describer = DefaultLinkDescriber.create(readmeElement("awesome.html")!!)
        val descriptions = describer(link)

        descriptions.forEach {
            when (it) {
                is LinkDescription.Title -> assertThat(it()).isEqualTo("Swift")
            }
        }
    }

    @Test fun noneSummaryStrategy() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val describer = DefaultLinkDescriber.create(
                readmeElement("awesome.html")!!, listOf(LinkDescriptionStrategies.none()))
        val descriptions = describer(link)

        assertThat(descriptions[0]).isEqualTo(LinkDescription.None(link))
    }

    @Test fun describePlentyAwesome() {
        val element = readmeElement("awesome.html");
        val linkExtractor = DefaultLinkExtractor.create(Html.links(element!!))
        val links = linkExtractor(Link.from("https://github.com/sindresorhus/awesome"))
        val describer = DefaultLinkDescriber.create(element)

        links.forEach {
            val descriptions = describer(it)
            descriptions.forEach {
                when (it) {
                    is LinkDescription.Title -> assertThat(it()).isNotEmpty()
                    is LinkDescription.Summary -> assertThat(it()).isNotEmpty()
                }
            }
        }
    }

    @Test fun describePlentyKotlin() {
        val element = readmeElement("awesome-kotlin.html");
        val linkExtractor = DefaultLinkExtractor.create(Html.links(element!!))
        val links = linkExtractor(Link.from("https://github.com/JavaBy/awesome-kotlin"))
        val describer = DefaultLinkDescriber.create(element)

        links.forEach {
            val descriptions = describer(it)
            descriptions.forEach {
                when (it) {
                    is LinkDescription.Title -> assertThat(it()).isNotEmpty()
                    is LinkDescription.Summary -> assertThat(it()).isNotEmpty()
                }
            }
        }
    }
}
