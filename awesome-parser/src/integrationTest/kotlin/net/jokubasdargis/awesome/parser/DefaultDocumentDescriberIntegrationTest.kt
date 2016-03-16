package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Host
import net.jokubasdargis.awesome.core.Link
import org.junit.Test


class DefaultDocumentDescriberIntegrationTest : BaseIntegrationTest() {

    @Test fun describeAwesome() {
        val describer = DefaultDocumentDescriber.create(readmeElement("awesome.html")!!)
        val descriptions = describer(Link.from("https://github.com/sindresorhus/awesome"))

        assertThat(descriptions).hasSize(3)

        descriptions.forEach {
            when (it) {
                is DocumentDescription.Links -> {
                    assertThat(it()).hasSize(318)
                    assertThat(it.ofHost(Host.GITHUB)).hasSize(311)
                    assertThat(it.notOfHost(Host.GITHUB)).hasSize(7)
                    assertThat(it.identified()).hasSize(318)
                    assertThat(it.invalid()).hasSize(0)
                }
                is DocumentDescription.LinkRelationships -> {
                    assertThat(it()).hasSize(344)
                }
                is DocumentDescription.LinkDescriptions -> {
                    assertThat(it()).hasSize(318)
                }
            }
        }
    }

    @Test fun describeTrapLinks() {
        val describer = DefaultDocumentDescriber.create(document("trap-links.html"))
        val descriptions = describer(Link.from("http://localhost"))

        assertThat(descriptions).hasSize(3)

        descriptions.forEach {
            when (it) {
                is DocumentDescription.Links -> {
                    assertThat(it()).hasSize(5)
                    assertThat(it.identified()).hasSize(4)
                    assertThat(it.invalid()).hasSize(1)
                    assertThat(it.ofHost(Host.GITHUB)).hasSize(0)
                    assertThat(it.notOfHost(Host.GITHUB)).hasSize(4)
                }
                is DocumentDescription.LinkRelationships -> {
                    assertThat(it()).hasSize(1)
                }
                is DocumentDescription.LinkDescriptions -> {
                    assertThat(it()).hasSize(4)
                }
            }
        }
    }

    @Test fun describeMetamodeling() {
        val describer = DefaultDocumentDescriber.create(document("metamodeling.html"))
        val descriptions = describer(Link.from("https://en.wikipedia.org"))

        assertThat(descriptions).hasSize(3)

        descriptions.forEach {
            when (it) {
                is DocumentDescription.Links -> {
                    assertThat(it()).hasSize(286)
                    assertThat(it.identified()).hasSize(258)
                    assertThat(it.invalid()).hasSize(28)
                    assertThat(it.ofHost(Host.GITHUB)).hasSize(0)
                    assertThat(it.notOfHost(Host.GITHUB)).hasSize(257)
                }
                is DocumentDescription.LinkRelationships -> {
                    assertThat(it()).hasSize(270)
                }
                is DocumentDescription.LinkDescriptions -> {
                    assertThat(it()).hasSize(257)
                }
            }
        }
    }
}
