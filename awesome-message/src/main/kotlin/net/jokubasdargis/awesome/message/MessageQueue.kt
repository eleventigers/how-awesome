package net.jokubasdargis.awesome.message

import java.io.Closeable

interface MessageQueue<T>: MutableIterable<T>, Closeable {
    fun add(value: T): Boolean
    val size: Long
}