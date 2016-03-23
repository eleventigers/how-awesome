package net.jokubasdargis.awesome.crawler

import java.io.OutputStream

interface MessageConverter<T> {
    fun from(bytes: ByteArray): Result<T>
    fun toStream(o: T, stream: OutputStream): Result<Int>
}
