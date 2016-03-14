package net.jokubasdargis.awesome.parser

import org.jsoup.nodes.Element

internal class DefaultLinkDescriber private constructor(
        private val element: Element,
        private val strategies: List<(Link) -> (Element) -> LinkDescription>) : LinkDescriber {

    override fun describe(value: Link): List<LinkDescription> {
        val firstElement = if (!value.raw().isBlank()) element
                .getElementsByAttributeValueContaining(Html.Attr.HREF.value, value.raw())
                .firstOrNull() else null

        if (firstElement != null) {
            return strategies.map { it(value)(firstElement) }
        } else {
            return emptyList()
        }
    }

    companion object {
        fun create(element: Element, strategies: List<(Link) -> (Element) -> LinkDescription> =
        listOf(LinkDescriptionStrategies.title(), LinkDescriptionStrategies.summary()))
                : LinkDescriber {
            return DefaultLinkDescriber(element, strategies)
        }
    }
}