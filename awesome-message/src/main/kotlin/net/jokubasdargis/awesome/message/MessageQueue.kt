package net.jokubasdargis.awesome.message

import java.io.Closeable

interface MessageQueue<T>: Closeable {
    fun add(value: MessageParcel<T>): Boolean
    fun peek(): MessageParcel<T>?
    fun remove()
    fun isEmpty(): Boolean
    val size: Long
}
