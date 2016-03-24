package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.Test

class InlineLinkDefinerIntegrationTest : BaseIntegrationTest() {

    @Test fun defineKotlin() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val definer = InlineLinkDefiner.create(readmeElement("awesome.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("Kotlin")
            }
        }
    }

    @Test fun defineUnity() {
        val link = Link.from("https://github.com/RyanNielson/awesome-unity")
        val definer = InlineLinkDefiner.create(readmeElement("awesome.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("Unity")
                is LinkDefinition.Description -> assertThat(it()).isEqualTo("(Game engine)")
            }
        }
    }

    @Test fun defineKtor() {
        val link = Link.from("https://github.com/Kotlin/ktor")
        val definer = InlineLinkDefiner.create(readmeElement("awesome-kotlin.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("Kotlin/ktor")
                is LinkDefinition.Description -> assertThat(it())
                        .isEqualTo("Web backend framework for Kotlin")
            }
        }
    }

    @Test fun defineKotlinDaggerExample() {
        val link = Link.from("https://github.com/damianpetla/kotlin-dagger-example")
        val definer = InlineLinkDefiner.create(readmeElement("awesome-kotlin.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it())
                        .isEqualTo("damianpetla/kotlin-dagger-example")
                is LinkDefinition.Description -> assertThat(it()).isEqualTo(
                        "Example of Android project showing integration with Kotlin and Dagger 2")
            }
        }
    }

    @Test fun defineBrikk() {
        val link = Link.from("https://github.com/brikk/brikk")
        val definer = InlineLinkDefiner.create(readmeElement("awesome-kotlin.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("brikk/brikk")
                is LinkDefinition.Description -> assertThat(it())
                        .isEqualTo("Brikk dependency manager (Kotlin, KotlinJS, Java, …​)")
            }
        }
    }

    @Test fun defineOkHttp() {
        val link = Link.from("http://square.github.io/okhttp")
        val definer = InlineLinkDefiner.create(readmeElement("awesome-java.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("OkHttp")
                is LinkDefinition.Description -> assertThat(it()).isEqualTo("HTTP+SPDY client")
            }
        }
    }

    @Test fun defineSwift() {
        val link = Link.from("https://github.com/matteocrippa/awesome-swift")
        val definer = InlineLinkDefiner.create(readmeElement("awesome.html")!!)
        val definitions = definer(link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("Swift")
            }
        }
    }

    @Test fun noneSummaryStrategy() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val definer = InlineLinkDefiner.create(
                readmeElement("awesome.html")!!, listOf(LinkDefinitionStrategies.none()))
        val definitions = definer(link)

        assertThat(definitions).hasSize(1)
        assertThat(definitions[0]).isEqualTo(LinkDefinition.None(link))
    }

    @Test fun definePlentyAwesome() {
        val element = readmeElement("awesome.html");
        val linkExtractor = DefaultLinkExtractor.create(Html.links(element!!))
        val links = linkExtractor(Link.from("https://github.com/sindresorhus/awesome"))
        val definer = InlineLinkDefiner.create(element)

        links.forEach {
            val definitions = definer(it)
            assertThat(definitions).hasSize(2)
            definitions.forEach {
                when (it) {
                    is LinkDefinition.Title -> assertThat(it()).isNotEmpty()
                    is LinkDefinition.Description -> assertThat(it()).isNotEmpty()
                }
            }
        }
    }

    @Test fun definePlentyKotlin() {
        val element = readmeElement("awesome-kotlin.html");
        val linkExtractor = DefaultLinkExtractor.create(Html.links(element!!))
        val links = linkExtractor(Link.from("https://github.com/JavaBy/awesome-kotlin"))
        val definer = InlineLinkDefiner.create(element)

        links.forEach {
            val definitions = definer(it)
            assertThat(definitions).hasSize(2)
            definitions.forEach {
                when (it) {
                    is LinkDefinition.Title -> assertThat(it()).isNotEmpty()
                    is LinkDefinition.Description -> assertThat(it()).isNotEmpty()
                }
            }
        }
    }
}
