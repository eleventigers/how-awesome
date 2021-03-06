package net.jokubasdargis.awesome.message

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal class LazyMessageRouter private constructor(
        private val queueFactories: Map<KClass<*>, () -> MessageQueue<*>>) : MessageRouter {

    private val lazyQueues = ConcurrentHashMap<KClass<*>, Lazy<MessageQueue<*>>>()

    @Suppress("UNCHECKED_CAST", "USELESS_CAST")
    private fun <T : Any> queue(kClass: KClass<T>): MessageQueue<T>? {
        synchronized(lazyQueues) {
            val lazyQueue = lazyQueues[kClass as KClass<Any>]
            if (lazyQueue != null) {
                return lazyQueue.value as MessageQueue<T>
            } else {
                val factory = queueFactories[kClass]
                if (factory != null) {
                    lazyQueues.put(kClass, lazy (LazyThreadSafetyMode.SYNCHRONIZED) { factory() })
                    return queue(kClass as KClass<T>)
                } else {
                    return null
                }
            }
        }
    }

    override fun <T : Any> route(kClass: KClass<T>): MessageQueue<T> {
        val q = queue(kClass)
        if (q != null) {
            return q
        } else {
            return object : MessageQueue<T> {
                private val logger = LoggerFactory.getLogger(LazyMessageRouter::class.java)

                override fun add(value: MessageParcel<T>): Boolean {
                    warn()
                    return false
                }

                override val size: Long
                    get() = let {
                        warn()
                        0
                    }

                override fun peek(): MessageParcel<T>? {
                    warn()
                    return null
                }

                override fun remove() {
                    warn()
                }

                override fun isEmpty(): Boolean {
                    warn()
                    return true
                }

                override fun close() {
                    warn()
                }

                private fun warn() {
                    logger.warn("No-op when no queue bindings for ${kClass.simpleName} are found")
                }
            }
        }
    }

    override fun close() {
        synchronized(lazyQueues) {
            lazyQueues.forEach {
                if (it.value.isInitialized()) {
                    it.value.value.close()
                }
            }
            lazyQueues.clear()
        }
    }

    companion object {
        fun create(queueFactories: Map<KClass<*>, () -> MessageQueue<*>>): MessageRouter {
            return LazyMessageRouter(queueFactories)
        }
    }
}
