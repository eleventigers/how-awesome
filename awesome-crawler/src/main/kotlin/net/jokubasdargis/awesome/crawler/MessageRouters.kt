package net.jokubasdargis.awesome.crawler

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.parser.LinkDefinition
import java.net.URI
import kotlin.reflect.KClass

class MessageRouters private constructor() {
    companion object {

        private val EXCHANGE_CRAWLER = "net.jokubasdargis.awesome.crawler"
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

        fun awesome(uri: URI? = null): MessageRouter {
            val connection = let {
                val factory = ConnectionFactory()
                if (uri != null) {
                    factory.setUri(uri)
                }
                factory.newConnection()
            }

            val link = Pair(Link::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_CRAWLER, KEY_LINK,
                        ProtoMessageConverters.link())
            })
            val linkDefRelationship = Pair(LinkDefinition.Relationship::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_CRAWLER, KEY_LINK_RELATIONSHIP,
                        ProtoMessageConverters.linkDefinitionRelationship())
            })
            val linkDefTitle = Pair(LinkDefinition.Title::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_CRAWLER, KEY_LINK_TITLE,
                        ProtoMessageConverters.linkDefinitionTitle())
            })
            val linkDefDescription = Pair(LinkDefinition.Description::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_CRAWLER, KEY_LINK_DESCRIPTION,
                        ProtoMessageConverters.linkDefinitionDescription())
            })
            val linkDefStarsCount = Pair(LinkDefinition.StarsCount::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_CRAWLER, KEY_LINK_STARS_COUNT,
                        ProtoMessageConverters.linkDefinitionStarsCount())
            })
            val linkDefForksCount = Pair(LinkDefinition.ForksCount::class, {
                RabbitMqMessageQueue.create(connection, EXCHANGE_CRAWLER, KEY_LINK_FORKS_COUNT,
                        ProtoMessageConverters.linkDefinitionForksCount())
            })
            val linkDefLatestCommitDate = Pair(LinkDefinition.LatestCommitDate::class, {
                RabbitMqMessageQueue.create(connection,
                        EXCHANGE_CRAWLER, KEY_LINK_LATEST_COMMIT_DATE,
                        ProtoMessageConverters.linkDefinitionLatestCommitDate())
            })

            return CloseableConnectionMessageRouter(LazyMessageRouter.create(mapOf(
                    link, linkDefRelationship, linkDefTitle, linkDefDescription,
                    linkDefStarsCount, linkDefForksCount, linkDefLatestCommitDate)), connection)
        }

        fun noop(): MessageRouter {
            return LazyMessageRouter.create(emptyMap())
        }
    }
}

inline fun <reified T: Any>MessageRouter.routeFor(): MessageQueue<T> {
    return route(T::class)
}
