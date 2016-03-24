package net.jokubasdargis.awesome.message

import com.google.common.truth.Truth.assertThat
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.Link
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import java.util.UUID

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

    @Test fun addRemove() {
        val link = createLink()
        val added = sut.add(link)
        val removed = sut.iterator().next()

        assertThat(added).isTrue()
        assertThat(link).isEqualTo(removed)
    }

    @Test fun multipleAddRemove() {
        val count = 100
        val links = (1..count).map { createLink() }

        links.forEach { sut.add(it) }
        val removed = (1..count).map {
            sut.iterator().next()
        }

        assertThat(links).containsExactlyElementsIn(removed)
    }

    private fun createLink(): Link {
        return Link.from("https://google.com?q=${UUID.randomUUID()}")
    }
}