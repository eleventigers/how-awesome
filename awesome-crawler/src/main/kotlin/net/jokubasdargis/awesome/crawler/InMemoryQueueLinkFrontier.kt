package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

internal class InMemoryQueueLinkFrontier private constructor(
        private val queue: BlockingDeque<Link>) : LinkFrontier {

    override fun add(link: Link): Boolean {
        if (link !is Link.Identified) {
            return false
        }
        queue.put(link)
        return true
    }

    override fun remove() {
        queue.remove()
    }

    override fun peek(): Link? {
        synchronized(queue) {
            val link = queue.takeFirst()
            queue.putFirst(link)
            return link
        }
    }

    override fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    override fun toString(): String {
        return "DefaultLinkFrontier(size=$size, remainingCapacity=${queue.remainingCapacity()})"
    }

    override val size: Int
        get() = queue.size

    override fun close() {
        // no-op
    }

    companion object {
        fun create(capacity: Int = 1048576): LinkFrontier {
            return InMemoryQueueLinkFrontier(LinkedBlockingDeque(capacity))
        }
    }
}
