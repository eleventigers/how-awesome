package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.Test
import java.time.Instant

class DocumentLinkDefinerIntegrationTest : BaseIntegrationTest() {

    @Test fun defineAwesome() {
        val link = Link.from("https://github.com/sindresorhus/awesome")
        val definer = DocumentLinkDefiner.create(document("awesome.html"))
        val definitions = definer(link)

        assertThat(definitions).hasSize(5)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("sindresorhus/awesome")
                is LinkDefinition.Description -> assertThat(it())
                        .isEqualTo("A curated list of awesome lists")
                is LinkDefinition.StarsCount -> assertThat(it()).isEqualTo(31488)
                is LinkDefinition.ForksCount -> assertThat(it()).isEqualTo(3338)
                is LinkDefinition.LatestCommitDate -> assertThat(it())
                        .isEqualTo(Instant.parse("2016-03-10T13:27:28Z"))
            }
        }
    }

    @Test fun defineSwiftEducation() {
        val link = Link.from("https://github.com/hsavit1/Awesome-Swift-Education")
        val definer = DocumentLinkDefiner.create(document("awesome-swift-education.html"))
        val definitions = definer(link)

        assertThat(definitions).hasSize(5)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo(
                        "hsavit1/Awesome-Swift-Education")
                is LinkDefinition.Description -> assertThat(it())
                        .isEqualTo(":fire: All of the resources for Learning About Swift")
                is LinkDefinition.StarsCount -> assertThat(it()).isEqualTo(4336)
                is LinkDefinition.ForksCount -> assertThat(it()).isEqualTo(352)
                is LinkDefinition.LatestCommitDate -> assertThat(it())
                        .isEqualTo(Instant.parse("2016-03-17T01:40:41Z"))
            }
        }
    }

    @Test fun defineKotlin() {
        val link = Link.from("https://github.com/JavaBy/awesome-kotlin")
        val definer = DocumentLinkDefiner.create(document("awesome-kotlin.html"))
        val definitions = definer(link)

        assertThat(definitions).hasSize(5)
        definitions.forEach {
            when (it) {
                is LinkDefinition.Title -> assertThat(it()).isEqualTo("JavaBy/awesome-kotlin")
                is LinkDefinition.Description -> assertThat(it()).isEqualTo(
                                "A curated list of awesome Kotlin related stuff " +
                                "Inspired by awesome-java")
                is LinkDefinition.StarsCount -> assertThat(it()).isEqualTo(207)
                is LinkDefinition.ForksCount -> assertThat(it()).isEqualTo(13)
                is LinkDefinition.LatestCommitDate -> assertThat(it())
                        .isEqualTo(Instant.parse("2016-03-09T13:00:07Z"))
            }
        }
    }
}