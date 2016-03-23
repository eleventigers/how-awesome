package net.jokubasdargis.awesome.crawler

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference

internal class RabbitMqMessageQueue<T> private constructor(
        private val connection: Connection,
        private val exchangeName: String,
        private val routingKey: String,
        private val converter: MessageConverter<T>) : MessageQueue<T> {

    private val incoming = LinkedBlockingQueue<Pair<Long, T>>()

    private val queueName = AtomicReference<String>()

    private val channel: Lazy<Channel> = lazy(LazyThreadSafetyMode.SYNCHRONIZED, {
        val ch = connection.createChannel()
        val defaultExchange = exchangeName.isNullOrBlank()
        if (!defaultExchange) {
            ch.exchangeDeclare(exchangeName, "direct", true)
        }
        queueName.set(ch.queueDeclare().queue)
        if (!defaultExchange) {
            ch.queueBind(queueName.get(), exchangeName, routingKey)
        }
        ch.confirmSelect()
        ch
    })

    init {
        channel.value.basicConsume(queueName.get(), false, object : DefaultConsumer(channel.value) {
            override fun handleDelivery(consumerTag: String?, envelope: Envelope?,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val deliveryTag = envelope?.deliveryTag
                if (deliveryTag != null && body != null) {
                    val converted = converter.from(body)
                    when (converted) {
                        is Result.Success -> {
                            incoming.offer(Pair(deliveryTag, converted.value))
                        }
                        is Result.Failure -> {
                            LOGGER.error("Failed to convert bytes to value: ${converted.error}")
                            ack(deliveryTag)
                        }
                    }
                }
            }
        })
    }

    override fun add(value: T): Boolean {
        val stream = ByteArrayOutputStream()
        val converted = converter.toStream(value, stream)
        when (converted) {
            is Result.Success -> {
                try {
                    channel.value.basicPublish(
                            exchangeName, routingKey, null, stream.toByteArray())
                    return true
                } catch (e: IOException) {
                    LOGGER.error("Failed to send $value: ${e.cause}")
                    return false
                }
            }
            is Result.Failure -> {
                LOGGER.error("Failed to write $value to stream: ${converted.error}")
                return false
            }
        }
    }

    override fun iterator(): MutableIterator<T> {
        return object : AbstractIterator<T>(), MutableIterator<T> {
            override fun computeNext() {
                val parcel = incoming.take()
                if (ack(parcel.first)) {
                    setNext(parcel.second)
                }
            }

            override fun remove() {
                next()
            }
        }
    }

    private fun ack(tag: Long): Boolean {
        try {
            channel.value.basicAck(tag, false)
            return true
        } catch (e: IOException) {
            LOGGER.error("Failed to ack message: ${e.cause}")
            return false
        }
    }

    override val size: Long
        get() = try {
            channel.value.waitForConfirms()
            channel.value.messageCount(queueName.get())
        } catch (e: IOException) {
            LOGGER.error("Failed to get queue size: ${e.cause}")
            0L
        }

    override fun close() {
        try {
            if (channel.isInitialized()) {
                channel.value.close()
            }
        } catch(e: IOException) {
            LOGGER.error("Failure while closing channel: $e")
        }
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(RabbitMqMessageQueue::class.java)

        fun <T> create(connection: Connection, exchangeName: String = "", // default exchange
                       routingKey: String, converter: MessageConverter<T>): MessageQueue<T> {
            return RabbitMqMessageQueue(connection, exchangeName, routingKey, converter)
        }
    }
}