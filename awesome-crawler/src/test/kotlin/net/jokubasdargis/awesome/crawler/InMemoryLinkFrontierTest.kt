package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.junit.Test


class InMemoryLinkFrontierTest {

    companion object {
        private val LINK_A = Link.from("http://google.com")
        private val LINK_B = Link.from("http://jokubasdargis.net")
    }

    private val sut = InMemoryQueueLinkFrontier.create()

    @Test fun add() {
        val result = sut.add(LINK_A)

        assertThat(result).isTrue()
    }

    @Test fun addPeek() {
        sut.add(LINK_A)

        assertThat(sut.peek()).isEqualTo(LINK_A)
    }

    @Test fun multipleAddThenPeekRemove() {
        sut.add(LINK_A)
        sut.add(LINK_B)
        sut.add(LINK_B)
        sut.add(LINK_A)

        assertThat(sut.peek()).isEqualTo(LINK_A)
        sut.remove()
        assertThat(sut.peek()).isEqualTo(LINK_B)
        sut.remove()
        assertThat(sut.peek()).isEqualTo(LINK_B)
        sut.remove()
        assertThat(sut.peek()).isEqualTo(LINK_A)
        sut.remove()
    }
}