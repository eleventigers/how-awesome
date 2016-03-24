package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.jsoup.nodes.Element

internal class InlineLinkDefiner private constructor(
        private val element: Element,
        private val strategies: List<(Link) -> (Element) -> LinkDefinition>) :
        (Link) -> List<LinkDefinition> {

    override fun invoke(value: Link): List<LinkDefinition> {
        val firstElement = if (!value.raw.isBlank()) element
                .getElementsByAttributeValueContaining(Html.Attr.HREF.value, value.raw)
                .firstOrNull() else null

        if (firstElement != null) {
            return strategies.map { it(value)(firstElement) }
        } else {
            return emptyList()
        }
    }

    companion object {
        fun create(element: Element, strategies: List<(Link) -> (Element) -> LinkDefinition> =
        listOf(
                LinkDefinitionStrategies.inlineTitle(),
                LinkDefinitionStrategies.inlineDescription()
        )): (Link) -> List<LinkDefinition> {
            return InlineLinkDefiner(element, strategies)
        }
    }
}
