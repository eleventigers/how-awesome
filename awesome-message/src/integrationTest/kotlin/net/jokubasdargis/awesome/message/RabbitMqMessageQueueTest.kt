package net.jokubasdargis.awesome.message

import com.google.common.truth.Truth.assertThat
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.Link
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

@Ignore("depends on a running RabbitMQ broker")
class RabbitMqMessageQueueTest {

    companion object {
        private val EXCHANGE_NAME = "awesome"
        private val ROUTING_KEY = "test"
    }

    private val conn = ConnectionFactory().newConnection()
    private val sut = RabbitMqMessageQueue.create(
            conn, EXCHANGE_NAME, ROUTING_KEY, ProtoMessageConverters.link())

    @After fun tearDown() {
        sut.close()
        conn.close()
    }

    @Test fun addPeek() {
        val link = createLink()
        val added = sut.add(link)
        val peeked = sut.peek()

        assertThat(added).isTrue()
        assertThat(link).isEqualTo(peeked)
    }

    @Test fun multipleAddPeekRemove() {
        val count = 100
        val links = (1..count).map { createLink() }

        links.forEach { sut.add(it) }
        val removed = (1..count).map {
            val v = sut.peek()
            sut.remove()
            v
        }

        assertThat(links).containsExactlyElementsIn(removed)
    }

    private val counter = AtomicInteger()

    private fun createLink(): Link {
        return Link.from("https://google.com?q=${counter.andIncrement}")
    }
}