package net.jokubasdargis.awesome.message

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import java.net.URI
import kotlin.reflect.KClass

class MessageRouters private constructor() {
    companion object {

        private val EXCHANGE_AWESOME = "net.jokubasdargis.awesome"
        private val KEY_LINK = "link"
        private val KEY_LINK_RELATIONSHIP = "$KEY_LINK.relationship"
        private val KEY_LINK_TITLE = "$KEY_LINK.title"
        private val KEY_LINK_DESCRIPTION = "$KEY_LINK.desc"
        private val KEY_LINK_STARS_COUNT = "$KEY_LINK.stars.count"
        private val KEY_LINK_FORKS_COUNT = "$KEY_LINK.forks.count"
        private val KEY_LINK_LATEST_COMMIT_DATE = "$KEY_LINK.commit.latest.date"

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

            val link = Pair(Link::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_AWESOME, KEY_LINK,
                        ProtoMessageConverters.Companion.link())
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
