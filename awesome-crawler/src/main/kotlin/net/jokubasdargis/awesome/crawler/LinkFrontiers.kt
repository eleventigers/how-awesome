package net.jokubasdargis.awesome.crawler

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.message.MessageQueue
import net.jokubasdargis.awesome.message.RabbitMqMessageQueue
import java.io.File
import java.net.URI

class LinkFrontiers private constructor() {
    companion object {
        private class CloseableConnectionLinkFrontier(val linkFrontier: LinkFrontier,
                                                      val connection: Connection): LinkFrontier {

            override fun add(link: Link): Boolean {
                return linkFrontier.add(link)
            }

            override fun peek(): Link? {
                return linkFrontier.peek()
            }

            override fun remove() {
                linkFrontier.remove()
            }

            override fun isEmpty(): Boolean {
               return linkFrontier.isEmpty()
            }

            override val size: Int
                get() = linkFrontier.size

            override fun close() {
                linkFrontier.close()
                connection.close()
            }
        }


        @JvmStatic @JvmOverloads fun newInMemoryQueueLinkFrontier(
                capacity: Int = 1048576): LinkFrontier {
            return InMemoryQueueLinkFrontier.create(capacity)
        }

        @JvmStatic fun newFileQueueLinkFrontier(file: File): LinkFrontier {
            return FileQueueLinkFrontier.create(file)
        }

        @JvmStatic fun newMessageQueueLinkFrontier(messageQueue: MessageQueue<Link>): LinkFrontier {
            return MessageQueueLinkFrontier.create(messageQueue);
        }

//        @JvmStatic fun newMessageQueueLinkFrontier(uri: URI? = null): LinkFrontier {
//            val connection = let {
//                val factory = ConnectionFactory()
//                if (uri != null) {
//                    factory.setUri(uri)
//                }
//                factory.isAutomaticRecoveryEnabled = true
//                factory.newConnection()
//            }
//
//            val queue = RabbitMqMessageQueue.create<Link>(connection, )
//        }
    }
}
