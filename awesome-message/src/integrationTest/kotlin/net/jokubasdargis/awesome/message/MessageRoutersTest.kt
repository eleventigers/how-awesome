package net.jokubasdargis.awesome.message

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import java.time.Instant
import java.util.UUID

@Ignore("depends on a running RabbitMQ broker")
class MessageRoutersTest {

    companion object {
        private val TIMESTAMP = Instant.ofEpochSecond(1488755237)
    }

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

        linkQueue.add(MessageParcel(link, TIMESTAMP))
        linkDefTitleQueue.add(MessageParcel(linkDefTitle, TIMESTAMP))

        val peekedLink = linkQueue.peek()
        val peekedLinkDefTitle = linkDefTitleQueue.peek()

        assertThat(peekedLink?.value).isEqualTo(link)
        assertThat(peekedLinkDefTitle?.value).isEqualTo(linkDefTitle)
    }

    @Test fun routeAny() {
        val queue = sut.routeFor<Any>()

        val added = queue.add(MessageParcel(Any(), TIMESTAMP))

        assertThat(added).isFalse()
    }

    @Test fun receive() {
        val links = sut.routeFor<Link>()

        while (!links.isEmpty()) {
            println(links.peek())
            links.remove()
        }
    }

    @After fun tearDown() {
        sut.close()
    }

    private fun createLink(): Link {
        return Link.from("https://google.com?q=${UUID.randomUUID()}")
    }
}