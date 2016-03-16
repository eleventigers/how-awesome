package net.jokubasdargis.awesome.parser

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class DefaultLinkExtractorTest {

    private companion object {
        private val ROOT_LINK = Link.from("https://github.com/sindresorhus/awesome")

        private const val PLATFORMS = "platforms"
        private const val TOC = "table-of-contents"

        private const val ID_PREFIX = "#"

        private fun makeId(value: String): String {
            return ID_PREFIX + value
        }
    }

    @Test fun singleLevelAnchorLinks() {
        val rootElement = mock(Element::class.java)
        val elementA = mock(Element::class.java)
        val elementB = mock(Element::class.java)
        val elements = Elements(listOf(elementA, elementB))

        `when`(elementA.attr(Html.Attr.HREF.value)).thenReturn(makeId(PLATFORMS))
        `when`(elementB.attr(Html.Attr.HREF.value)).thenReturn(makeId(TOC))
        `when`(rootElement.getElementsByAttribute(Html.Attr.HREF.value)).thenReturn(elements)

        val links = DefaultLinkExtractor.create(Html.links(rootElement))(ROOT_LINK)

        assertThat(links).hasSize(2)
        assertThat(links).isEqualTo(listOf(
                Link.from(makeId(PLATFORMS), ROOT_LINK),
                Link.from(makeId(TOC), ROOT_LINK)))
    }
}
