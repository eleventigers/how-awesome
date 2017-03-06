package net.jokubasdargis.awesome.message


import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkOccurrence
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class FileMessageQueueTest {

    companion object {
        private val TIMESTAMP = Instant.ofEpochSecond(1488755237)
        private val COUNTER = AtomicInteger()

        private fun createLinkOccurrence(): LinkOccurrence {
            return LinkOccurrence(
                    Link.from("https://google.com?q=${COUNTER.andIncrement}"),
                    Link.from("https://caster.io"))
        }
    }

    private val tmpDir = Files.createTempDirectory(FileMessageQueueTest::class.simpleName)
    private val linkOccurrencesFile = File(tmpDir.toFile(), "link_occurrences")
    private val sut = FileMessageQueue.create(
            linkOccurrencesFile, ProtoMessageConverters.linkOccurrence())

    @Test fun add() {
        val linkOccurrence = createLinkOccurrence()
        val added = sut.add(MessageParcel(linkOccurrence, TIMESTAMP))

        assertThat(added).isTrue()
    }

    @Test fun addPeek() {
        val linkOccurrence = createLinkOccurrence()

        sut.add(MessageParcel(linkOccurrence, TIMESTAMP))
        val peeked = sut.peek()

        assertThat(linkOccurrence).isEqualTo(peeked?.value)
    }

    @Test fun addRemove() {
        val linkOccurrence = createLinkOccurrence()

        sut.add(MessageParcel(linkOccurrence, TIMESTAMP))
        sut.remove()
        val peeked = sut.peek()

        assertThat(peeked).isNull()
        assertThat(sut.size).isEqualTo(0)
    }

    @Test fun multipleAddPeekRemove() {
        val count = 100
        val links = (1..count).map { createLinkOccurrence() }

        links.forEach { sut.add(MessageParcel(it, TIMESTAMP)) }
        val removed = (1..count).map {
            val v = sut.peek()
            sut.remove()
            v?.value
        }

        assertThat(links).containsExactlyElementsIn(removed)
    }
}