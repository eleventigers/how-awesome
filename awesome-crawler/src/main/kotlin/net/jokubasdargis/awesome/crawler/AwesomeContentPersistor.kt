package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.message.MessageRouter
import net.jokubasdargis.awesome.message.routeFor

internal class AwesomeContentPersistor private constructor(
        private val messageRouter: MessageRouter) : (Iterable<DocumentDefinition>) -> Unit {

    override fun invoke(definitions: Iterable<DocumentDefinition>) {
        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    it().forEach {
                        messageRouter.routeFor<Link>().add(it)
                    }
                }
                is DocumentDefinition.LinkDefinitions -> {
                    it().forEach {
                        messageRouter.route(it.javaClass.kotlin).add(it)
                    }
                }
            }
        }
    }

    companion object {
        fun create(messageRouter: MessageRouter): (Iterable<DocumentDefinition>) -> Unit {
            return AwesomeContentPersistor(messageRouter)
        }
    }
}
