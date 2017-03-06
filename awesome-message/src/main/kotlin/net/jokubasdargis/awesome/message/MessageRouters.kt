package net.jokubasdargis.awesome.message

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.LinkDefinition
import net.jokubasdargis.awesome.core.LinkOccurrence
import java.net.URI
import kotlin.reflect.KClass

class MessageRouters private constructor() {
    companion object {

        private val EXCHANGE_AWESOME = "net.jokubasdargis.awesome"
        private val PREFIX_LINK = "link"
        private val KEY_LINK_OCCURRENCE = "$PREFIX_LINK.occurrence"
        private val KEY_LINK_RELATIONSHIP = "$PREFIX_LINK.relationship"
        private val KEY_LINK_TITLE = "$PREFIX_LINK.title"
        private val KEY_LINK_DESCRIPTION = "$PREFIX_LINK.desc"
        private val KEY_LINK_STARS_COUNT = "$PREFIX_LINK.stars.count"
        private val KEY_LINK_FORKS_COUNT = "$PREFIX_LINK.forks.count"
        private val KEY_LINK_LATEST_COMMIT_DATE = "$PREFIX_LINK.commit.latest.date"

        private class CloseableConnectionMessageRouter (
                private val router: MessageRouter,
                private val connection: Connection): MessageRouter {

            override fun <T : Any> route(kClass: KClass<T>): MessageQueue<T> {
                return router.route(kClass)
            }

            override fun close() {
                router.close()
                connection.close()
            }
        }

        @JvmStatic @JvmOverloads fun awesome(uri: URI? = null): MessageRouter {
            val connection = let {
                val factory = ConnectionFactory()
                if (uri != null) {
                    factory.setUri(uri)
                }
                factory.isAutomaticRecoveryEnabled = true
                factory.newConnection()
            }

            val link = Pair(LinkOccurrence::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK_OCCURRENCE,
                        ProtoMessageConverters.Companion.linkOccurrence())
            })
            val linkDefRelationship = Pair(LinkDefinition.Relationship::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK_RELATIONSHIP,
                        ProtoMessageConverters.Companion.linkDefinitionRelationship())
            })
            val linkDefTitle = Pair(LinkDefinition.Title::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK_TITLE,
                        ProtoMessageConverters.Companion.linkDefinitionTitle())
            })
            val linkDefDescription = Pair(LinkDefinition.Description::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK_DESCRIPTION,
                        ProtoMessageConverters.Companion.linkDefinitionDescription())
            })
            val linkDefStarsCount = Pair(LinkDefinition.StarsCount::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK_STARS_COUNT,
                        ProtoMessageConverters.Companion.linkDefinitionStarsCount())
            })
            val linkDefForksCount = Pair(LinkDefinition.ForksCount::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK_FORKS_COUNT,
                        ProtoMessageConverters.Companion.linkDefinitionForksCount())
            })
            val linkDefLatestCommitDate = Pair(LinkDefinition.LatestCommitDate::class, {
                RabbitMqMessageQueue.create(connection,
                        EXCHANGE_AWESOME, KEY_LINK_LATEST_COMMIT_DATE,
                        ProtoMessageConverters.Companion.linkDefinitionLatestCommitDate())
            })

            return CloseableConnectionMessageRouter(LazyMessageRouter.create(mapOf(
                    link, linkDefRelationship, linkDefTitle, linkDefDescription,
                    linkDefStarsCount, linkDefForksCount, linkDefLatestCommitDate)), connection)
        }

        @JvmStatic fun noop(): MessageRouter {
            return LazyMessageRouter.create(emptyMap())
        }
    }
}

inline fun <reified T: Any> MessageRouter.routeFor(): MessageQueue<T> {
    return route(T::class)
}
