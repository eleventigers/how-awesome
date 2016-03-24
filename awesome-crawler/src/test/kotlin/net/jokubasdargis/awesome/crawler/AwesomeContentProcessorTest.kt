package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verifyZeroInteractions
import java.io.InputStream

class AwesomeContentProcessorTest {

    companion object {
        private val LINK_A = Link.from("http://github.com/eleventigers/awesome")
        private val LINK_B = Link.from("http://github.com/eleventigers/awesome#kotlin")
        private val LINK_C = Link.from("http://github.com/eleventigers/awesome#toc")
        private val LINK_INVALID = Link.from("")

        open private class EmptyListDocumentDescriberFactory :
                (InputStream) -> (Link) -> Iterable<DocumentDefinition> {
            override fun invoke(stream: InputStream): (Link) -> Iterable<DocumentDefinition> {
                return { emptyList() }
            }
        }

        open private class NoopAwesomeDocumentDescriptionPersistor :
                (Iterable<DocumentDefinition>) -> Unit {
            var called: Iterable<DocumentDefinition>? = null
            //TODO(eleventigers, 18/03/16): figure out why spy fails and I need to use this hack
            override fun invoke(descriptions: Iterable<DocumentDefinition>): Unit {
                called = descriptions
            }
        }
    }

    @Test
    fun supportedContentTypes() {
        val sut = AwesomeContentProcessor.create()

        assertThat(sut.supportedContentTypes()).containsExactly(ContentTypes.html())
    }

    @Test
    fun linksWithRelationships() {
        val links = DocumentDefinition.Links(LINK_A, setOf(LINK_A, LINK_B, LINK_C))
        val linkDefinitions = DocumentDefinition.LinkDefinitions(LINK_A, setOf(
                LinkDefinition.Relationship(LINK_C, LINK_B),
                LinkDefinition.Relationship(LINK_C, LINK_A)
        ))
        val documentDefinitions: Iterable<DocumentDefinition> = listOf(links, linkDefinitions)
        val stream = mock(InputStream::class.java)
        val sut = AwesomeContentProcessor.create { stream -> { link -> documentDefinitions } }
        val result = sut(stream, LINK_A)

        val expectedLinks = DocumentDefinition.Links(LINK_A, links.identified().toSet())
        val expectedLinkDefinitions = linkDefinitions

        assertThat(result).containsExactly(expectedLinks, expectedLinkDefinitions)
    }

    @Test fun invalidBaseLink() {
        val stream = mock(InputStream::class.java)
        val factory = `spy`(EmptyListDocumentDescriberFactory())
        val sut = AwesomeContentProcessor.create(factory)

        val result = sut(stream, LINK_INVALID)

        assertThat(result).isEmpty()
        verifyZeroInteractions(factory)
    }

    @Test fun withPersistor() {
        val stream = mock(InputStream::class.java)
        val persistor = NoopAwesomeDocumentDescriptionPersistor()
        val sut = AwesomeContentProcessor.create().withPersistor(persistor)

        val result = sut(stream, LINK_INVALID)

        assertThat(result).isEmpty()
        assertThat(persistor.called).isEqualTo(emptyList<DocumentDefinition>())
    }
}