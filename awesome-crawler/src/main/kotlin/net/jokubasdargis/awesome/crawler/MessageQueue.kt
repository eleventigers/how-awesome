package net.jokubasdargis.awesome.crawler

import java.io.Closeable

interface MessageQueue<T>: MutableIterable<T>, Closeable {
    fun add(value: T): Boolean
    val size: Long
}
