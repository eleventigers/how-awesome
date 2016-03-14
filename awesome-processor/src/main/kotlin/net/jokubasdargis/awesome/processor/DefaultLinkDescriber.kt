package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal class DefaultLinkDescriber private constructor(
        private val document: Document,
        private val strategies: List<(Link) -> (Element) -> LinkDescription>) : LinkDescriber {

    override fun describe(value: Link): List<LinkDescription> {
        val firstElement = if (!value.raw().isBlank()) document
                .getElementsByAttributeValueContaining(Html.Attr.HREF.value, value.raw())
                .firstOrNull() else null

        if (firstElement != null) {
            return strategies.map { it(value)(firstElement) }
        } else {
            return emptyList()
        }
    }

    companion object {
        fun create(document: Document, strategies: List<(Link) -> (Element) -> LinkDescription> =
        listOf(LinkDescriptionStrategies.title(), LinkDescriptionStrategies.summary()))
                : LinkDescriber {
            return DefaultLinkDescriber(document, strategies)
        }
    }
}