package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkRelationship
import net.jokubasdargis.awesome.core.asOrphans
import net.jokubasdargis.awesome.core.identified
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
                (InputStream) -> (Link) -> Iterable<DocumentDescription> {
            override fun invoke(stream: InputStream): (Link) -> Iterable<DocumentDescription> {
                return { emptyList() }
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
        val links = DocumentDescription.Links(listOf(LINK_A, LINK_B, LINK_C))
        val relationships = DocumentDescription.LinkRelationships(listOf(
                LinkRelationship(LINK_C, LINK_B),
                LinkRelationship(LINK_C, LINK_A)
        ))
        val descriptions: Iterable<DocumentDescription> = listOf(links, relationships)
        val stream = mock(InputStream::class.java)
        val sut = AwesomeContentProcessor.create { stream -> { link -> descriptions } }
        val result = sut(stream, LINK_A)

        val expectedLinks = AwesomeDocumentDescription.Links(LINK_A, links.identified().toSet())
        val expectedRelationships = AwesomeDocumentDescription
                .LinkRelationships(LINK_A, relationships().identified().asOrphans().toSet())

        assertThat(result).containsExactly(expectedLinks, expectedRelationships)
    }

    @Test fun invalidBaseLink() {
        val stream = mock(InputStream::class.java)
        val factory = spy(EmptyListDocumentDescriberFactory())
        val sut = AwesomeContentProcessor.create(factory)

        val result = sut(stream, LINK_INVALID)

        assertThat(result).isEmpty()
        verifyZeroInteractions(factory)
    }
}