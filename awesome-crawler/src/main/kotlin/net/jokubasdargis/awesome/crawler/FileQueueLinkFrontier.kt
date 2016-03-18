package net.jokubasdargis.awesome.crawler

import com.squareup.tape.FileObjectQueue
import net.jokubasdargis.awesome.core.Link
import java.io.File
import java.io.OutputStream
import java.util.NoSuchElementException

internal class FileQueueLinkFrontier private constructor(
        val queue: FileObjectQueue<Link>) : LinkFrontier {

    override fun add(link: Link): Boolean {
        if (link !is Link.Identified) {
            return false
        }
        try {
            queue.add(link)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override val size: Int
        get() = queue.size()

    override fun hasNext(): Boolean {
        return size > 0
    }

    override fun next(): Link {
        val link = queue.peek()
        if (link != null) {
            queue.remove()
            return link
        } else {
            // TODO(eleventigers, 18/03/16): block instead of throwing
            throw NoSuchElementException(
                    "Queue is empty, ensure #hasNext() returns true before calling #next()")
        }
    }

    override fun toString(): String{
        return "FileQueueLinkFrontier(size=$size)"
    }

    companion object {
        private class LinkConverter : FileObjectQueue.Converter<Link> {
            override fun toStream(link: Link?, stream: OutputStream?) {
                if (link != null && link is Link.Identified && stream != null) {
                    val string = link.toUri().toString()
                    stream.write(string.toByteArray())
                }
            }

            override fun from(bytes: ByteArray?): Link? {
                if (bytes != null) {
                    val string = String(bytes)
                    return Link.from(string)
                } else {
                    return null
                }
            }
        }

        fun create(file: File): LinkFrontier {
            return FileQueueLinkFrontier(FileObjectQueue(file, LinkConverter()))
        }
    }
}