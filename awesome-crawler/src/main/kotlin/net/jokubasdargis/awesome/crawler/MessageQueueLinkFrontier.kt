package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.message.MessageParcel
import net.jokubasdargis.awesome.message.MessageQueue
import java.time.Clock


internal class MessageQueueLinkFrontier private constructor(
        private val messageQueue: MessageQueue<Link>, private val clock: Clock): LinkFrontier {

    override fun add(link: Link): Boolean {
        return messageQueue.add(MessageParcel(link, clock.instant()))
    }

    override fun remove() {
        messageQueue.remove()
    }

    override fun isEmpty(): Boolean {
        return messageQueue.isEmpty()
    }

    override fun peek(): Link? {
        return messageQueue.peek()?.value
    }

    override val size: Int
        get() = messageQueue.size.toInt()


    override fun close() {
        messageQueue.close()
    }

    companion object {
        fun create(messageQueue: MessageQueue<Link>,
                   clock: Clock = Clock.systemUTC()): LinkFrontier {
            return MessageQueueLinkFrontier(messageQueue, clock)
        }
    }
}
