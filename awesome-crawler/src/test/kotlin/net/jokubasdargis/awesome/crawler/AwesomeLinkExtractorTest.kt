package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.Mockito.verifyZeroInteractions
import java.io.InputStream

class AwesomeLinkExtractorTest {

    companion object {
        private val LINK_A = Link.from("http://github.com/eleventigers/awesome")
        private val LINK_B = Link.from("http://github.com/eleventigers/awesome#kotlin")
        private val LINK_C = Link.from("http://github.com/eleventigers/awesome#toc")
        private val LINK_INVALID = Link.from("")

        open private class EmptyListLinkExtractorFactory :
                (InputStream) -> (Link) -> Iterable<Link> {
            override fun invoke(stream: InputStream): (Link) -> Iterable<Link> {
                return { emptyList() }
            }
        }
    }

    @Test fun supportedContentTypes() {
        val sut = AwesomeLinkExtractor.create()

        assertThat(sut.supportedContentTypes()).containsExactly(ContentTypes.html())
    }

    @Test fun identifiedLinks() {
        val links = listOf(LINK_A, LINK_B, LINK_C)
        val stream = Mockito.mock(InputStream::class.java)
        val sut = AwesomeLinkExtractor.create { stream -> { links } }

        val result = sut(stream, LINK_A)

        assertThat(result).isEqualTo(links)
    }

    @Test fun invalidBaseLink() {
        val stream = Mockito.mock(InputStream::class.java)
        val factory = `spy`(EmptyListLinkExtractorFactory())
        val sut = AwesomeLinkExtractor.create(factory)

        val result = sut(stream, LINK_INVALID)

        assertThat(result).isEmpty()
        verifyZeroInteractions(factory)
    }
}
