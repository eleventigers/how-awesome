package net.jokubasdargis.awesome.message

import java.io.Closeable
import kotlin.reflect.KClass

interface MessageRouter : Closeable {
    fun <T : Any> route(kClass: KClass<T>): MessageQueue<T>
}
