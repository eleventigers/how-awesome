package net.jokubasdargis.awesome.message

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import java.util.UUID

@Ignore("depends on a running RabbitMQ broker")
class MessageRoutersTest {

    private val sut = MessageRouters.awesome()

    @Test fun routePerClass() {
        sut.route(Link::class)
        sut.route(LinkDefinition.Title::class)
        sut.route(LinkDefinition.Description::class)
        sut.route(LinkDefinition.StarsCount::class)
        sut.route(LinkDefinition.ForksCount::class)
        sut.route(LinkDefinition.LatestCommitDate::class)
    }

    @Test fun routeGeneric() {
        sut.routeFor<Link>()
    }

    @Test fun routeMultiple() {
        val linkQueue = sut.routeFor<Link>()
        val linkDefTitleQueue = sut.routeFor<LinkDefinition.Title>()

        val link = createLink()
        val linkDefTitle = LinkDefinition.Title(link, "awesome")

        linkQueue.add(link)
        linkDefTitleQueue.add(linkDefTitle)

        val removedLink = linkQueue.iterator().next()
        val removedLinkDefTitle = linkDefTitleQueue.iterator().next()

        assertThat(removedLink).isEqualTo(link)
        assertThat(removedLinkDefTitle).isEqualTo(linkDefTitle)
    }

    @Test fun routeAny() {
        val queue = sut.routeFor<Any>()

        val added = queue.add(Any())

        assertThat(added).isFalse()
    }

    @Test fun receive() {
        val links = sut.routeFor<Link>()

        links.forEach {
            println(it)
        }
    }

    @After fun tearDown() {
        sut.close()
    }

    private fun createLink(): Link {
        return Link.from("https://google.com?q=${UUID.randomUUID()}")
    }
}