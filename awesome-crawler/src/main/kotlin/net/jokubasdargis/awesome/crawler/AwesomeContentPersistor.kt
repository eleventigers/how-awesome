package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.LinkDefinition
import net.jokubasdargis.awesome.core.LinkOccurrence
import net.jokubasdargis.awesome.message.MessageParcel
import net.jokubasdargis.awesome.message.MessageRouter
import net.jokubasdargis.awesome.message.routeFor
import java.time.Clock

internal class AwesomeContentPersistor private constructor(
        private val messageRouter: MessageRouter,
        private val clock: Clock) : (Iterable<DocumentDefinition>) -> Unit {

    override fun invoke(definitions: Iterable<DocumentDefinition>) {
        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> {
                    it().forEach { link ->
                        messageRouter.routeFor<LinkOccurrence>().add(
                                parcel(LinkOccurrence(link, it.documentLink)))
                    }
                }
                is DocumentDefinition.LinkDefinitions -> {
                    it().forEach {
                        when(it) {
                            is LinkDefinition.Title ->  messageRouter
                                    .route(LinkDefinition.Title::class).add(parcel(it))
                            is LinkDefinition.Description -> messageRouter
                                    .route(LinkDefinition.Description::class).add(parcel(it))
                            is LinkDefinition.Relationship -> messageRouter
                                    .route(LinkDefinition.Relationship::class).add(parcel(it))
                            is LinkDefinition.LatestCommitDate -> messageRouter
                                    .route(LinkDefinition.LatestCommitDate::class).add(parcel(it))
                            is LinkDefinition.ForksCount -> messageRouter
                                    .route(LinkDefinition.ForksCount::class).add(parcel(it))
                            is LinkDefinition.StarsCount -> messageRouter
                                    .route(LinkDefinition.StarsCount::class).add(parcel(it))
                        }
                    }
                }
            }
        }
    }

    private fun <T> parcel(value: T): MessageParcel<T> {
        return MessageParcel(value, clock.instant())
    }

    companion object {
        fun create(messageRouter: MessageRouter,
                   clock: Clock = Clock.systemUTC()): (Iterable<DocumentDefinition>) -> Unit {
            return AwesomeContentPersistor(messageRouter, clock)
        }
    }
}
