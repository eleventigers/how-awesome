package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import java.io.Closeable

interface LinkFrontier : Closeable {
    fun add(link: Link): Boolean
    fun peek(): Link?
    fun remove()
    fun isEmpty(): Boolean
    val size: Int
}