package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

internal class InMemoryQueueLinkFrontier private constructor(
        private val queue: BlockingQueue<Link>) : LinkFrontier {

    override fun add(link: Link): Boolean {
        if (link !is Link.Identified) {
            return false
        }
        queue.put(link)
        return true
    }

    override fun next(): Link {
        return queue.take()
    }

    override fun hasNext(): Boolean {
        return !queue.isEmpty()
    }

    override fun toString(): String{
        return "DefaultLinkFrontier(size=$size, remainingCapacity=${queue.remainingCapacity()})"
    }

    override val size: Int
        get() = queue.size

    companion object {
        fun create(capacity: Int = 1048576): LinkFrontier {
            return InMemoryQueueLinkFrontier(LinkedBlockingQueue(capacity))
        }
    }
}
