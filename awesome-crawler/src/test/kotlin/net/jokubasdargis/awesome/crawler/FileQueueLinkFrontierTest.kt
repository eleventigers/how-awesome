package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.NoSuchElementException


class FileQueueLinkFrontierTest {

    companion object {
        private val LINK_A = Link.from("http://google.com")
        private val LINK_B = Link.from("http://jokubasdargis.net")
        private val FILENAME_FRONTIER = "frontier"
    }

    @JvmField @Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun add() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)
        val result = sut.add(LINK_A)

        assertThat(result).isTrue()
    }

    @Test
    fun size() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        sut.add(LINK_B)

        val size = sut.size

        assertThat(size).isEqualTo(2)
    }

    @Test
    fun isEmpty() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        val afterAdd = sut.isEmpty()
        sut.remove()
        val afterRemove = sut.isEmpty()

        assertThat(afterAdd).isFalse()
        assertThat(afterRemove).isTrue()
    }

    @Test
    fun peek() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        val peeked = sut.peek()

        assertThat(peeked).isEqualTo(LINK_A)
    }

    @Test
    fun multipleAddThenPeekRemove() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        sut.add(LINK_B)
        sut.add(LINK_A)
        sut.add(LINK_A)

        assertThat(sut.peek()).isEqualTo(LINK_A)
        sut.remove()
        assertThat(sut.peek()).isEqualTo(LINK_B)
        sut.remove()
        assertThat(sut.peek()).isEqualTo(LINK_A)
        sut.remove()
        assertThat(sut.peek()).isEqualTo(LINK_A)
        sut.remove()
    }

    @Test
    fun peekWhenIsEmpty() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        val peeked = sut.peek()
        assertThat(peeked).isNull()
    }
}
