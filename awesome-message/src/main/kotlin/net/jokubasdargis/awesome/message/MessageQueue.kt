package net.jokubasdargis.awesome.message

import java.io.Closeable

interface MessageQueue<T>: Closeable {
    fun add(value: T): Boolean
    fun peek(): T?
    fun remove()
    fun isEmpty(): Boolean
    val size: Long
}
