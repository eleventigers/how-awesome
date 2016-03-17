package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.junit.Test


class DefaultLinkFrontierTest {

    companion object {
        private val LINK_A = Link.from("http://google.com")
        private val LINK_B = Link.from("http://jokubasdargis.net")
    }

    private val sut = InMemoryQueueLinkFrontier.create()

    @Test fun offer() {
        val result = sut.add(LINK_A)

        assertThat(result).isTrue()
    }

    @Test fun offerRemove() {
        sut.add(LINK_A)
        val link = sut.iterator().next()

        assertThat(link).isEqualTo(LINK_A)
    }

    @Test fun multipleOfferRemove() {
        sut.add(LINK_A)
        sut.add(LINK_B)

        val iterator = sut.iterator()
        val firstLink = iterator.next()
        val secondLink = iterator.next()

        assertThat(firstLink).isEqualTo(LINK_A)
        assertThat(secondLink).isEqualTo(LINK_B)
    }
}