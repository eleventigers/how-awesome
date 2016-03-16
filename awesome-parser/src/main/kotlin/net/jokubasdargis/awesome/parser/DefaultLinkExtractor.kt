package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import org.jsoup.nodes.Element

internal class DefaultLinkExtractor private constructor(
        private val linkElements: () -> List<Element>) : (Link) -> List<Link> {

    override fun invoke(value: Link): List<Link> {
        return linkElements()
                .map { Link.from(Html.href(it), value) }
                .distinctBy { it.raw }
    }

    companion object {
        fun create(linkElements: () -> List<Element>): (Link) -> List<Link> {
            return DefaultLinkExtractor(linkElements)
        }
    }
}
