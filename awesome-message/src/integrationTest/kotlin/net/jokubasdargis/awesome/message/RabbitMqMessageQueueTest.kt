package net.jokubasdargis.awesome.message

import com.google.common.truth.Truth.assertThat
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkOccurrence
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import java.time.Instant
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

@Ignore("depends on a running RabbitMQ broker")
class RabbitMqMessageQueueTest {

    companion object {
        private val EXCHANGE_NAME = "awesome"
        private val ROUTING_KEY = "test"
        private val TIMESTAMP = Instant.ofEpochSecond(1488755237)
    }

    private val conn = ConnectionFactory().newConnection()
    private val sut = RabbitMqMessageQueue.create(
            conn, EXCHANGE_NAME, ROUTING_KEY, ProtoMessageConverters.linkOccurrence())

    @After fun tearDown() {
        sut.close()
        conn.close()
    }

    @Test fun addPeek() {
        val linkOccurrence = createLinkOccurrence()
        val added = sut.add(MessageParcel(linkOccurrence, TIMESTAMP))
        val peeked = sut.peek()

        assertThat(added).isTrue()
        assertThat(linkOccurrence).isEqualTo(peeked?.value)
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

    private val counter = AtomicInteger()

    private fun createLinkOccurrence(): LinkOccurrence {
        return LinkOccurrence(
                Link.from("https://google.com?q=${counter.andIncrement}"),
                Link.from("https://caster.io"))
    }
}