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
    fun hasNext() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        val afterAdd = sut.hasNext()
        sut.next()
        val afterNext = sut.hasNext()

        assertThat(afterAdd).isTrue()
        assertThat(afterNext).isFalse()
    }

    @Test
    fun next() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        val next = sut.next()

        assertThat(next).isEqualTo(LINK_A)
    }

    @Test
    fun multipleAddThenNext() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.add(LINK_A)
        sut.add(LINK_B)
        sut.add(LINK_A)
        sut.add(LINK_A)

        assertThat(sut.next()).isEqualTo(LINK_A)
        assertThat(sut.next()).isEqualTo(LINK_B)
        assertThat(sut.next()).isEqualTo(LINK_A)
        assertThat(sut.next()).isEqualTo(LINK_A)
    }

    @Test(expected = NoSuchElementException::class)
    fun nextWhenHasNextIsFalse() {
        val file = File(temporaryFolder.root, FILENAME_FRONTIER)
        val sut = FileQueueLinkFrontier.create(file)

        sut.next()
    }
}
