package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.parser.LinkDefinition
import org.junit.After
import org.junit.Test
import java.util.UUID

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

    @After fun tearDown() {
        sut.close()
    }

    private fun createLink(): Link {
        return Link.from("https://google.com?q=${UUID.randomUUID()}")
    }
}