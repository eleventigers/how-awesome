package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.parser.DocumentDefinition

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
        fun create(messageRouter: MessageRouter = MessageRouters.noop()):
                (Iterable<DocumentDefinition>) -> Unit {
            return AwesomeContentPersistor(messageRouter)
        }
    }
}
