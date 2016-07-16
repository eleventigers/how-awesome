package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.message.MessageQueue


internal class MessageQueueLinkFrontier private constructor(
        private val messageQueue: MessageQueue<Link>): LinkFrontier {

    override fun add(link: Link): Boolean {
        return messageQueue.add(link)
    }

    override fun remove() {
        messageQueue.remove()
    }

    override fun isEmpty(): Boolean {
        return messageQueue.isEmpty()
    }

    override fun peek(): Link? {
        return messageQueue.peek()
    }

    override val size: Int
        get() = messageQueue.size.toInt()


    override fun close() {
        messageQueue.close()
    }

    companion object {
        fun create(messageQueue: MessageQueue<Link>): LinkFrontier {
            return MessageQueueLinkFrontier(messageQueue)
        }
    }
}
