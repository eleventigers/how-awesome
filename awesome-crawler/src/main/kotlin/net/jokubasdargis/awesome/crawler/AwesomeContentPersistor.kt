package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.LinkDefinition
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
                        when(it) {
                            is LinkDefinition.Title ->  messageRouter
                                    .route(LinkDefinition.Title::class).add(it)
                            is LinkDefinition.Description -> messageRouter
                                    .route(LinkDefinition.Description::class).add(it)
                            is LinkDefinition.Relationship -> messageRouter
                                    .route(LinkDefinition.Relationship::class).add(it)
                            is LinkDefinition.LatestCommitDate -> messageRouter
                                    .route(LinkDefinition.LatestCommitDate::class).add(it)
                            is LinkDefinition.ForksCount -> messageRouter
                                    .route(LinkDefinition.ForksCount::class).add(it)
                            is LinkDefinition.StarsCount -> messageRouter
                                    .route(LinkDefinition.StarsCount::class).add(it)
                        }
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
