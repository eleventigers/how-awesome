package net.jokubasdargis.awesome.message

import net.jokubasdargis.awesome.core.Link
import java.io.Closeable
import kotlin.reflect.KClass

interface MessageRouter : Closeable {
    fun <T : Any> route(kClass: KClass<T>): MessageQueue<T>
}

fun MessageRouter.links(): MessageQueue<Link> {
    return routeFor()
}
