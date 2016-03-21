package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal class DocumentLinkDefiner private constructor(
        private val document: Document,
        private val strategies: List<(Link) -> (Document) -> LinkDefinition>) :
        (Link) -> List<LinkDefinition> {

    override fun invoke(value: Link): List<LinkDefinition> {
        val elements = if (!value.raw.isBlank()) document
                .head()
                ?.getElementsByAttributeValueContaining(Html.Attr.HREF.value, value.raw)
        else emptyList<Element>()

        if (elements != null && elements.isNotEmpty()) {
            val canonical = elements
                    .filter { it.attr(Html.Attr.REL.value) == CANONICAL }
                    .firstOrNull()
            if (canonical != null) {
                return strategies.map { it(value)(document) }
            } else {
                return emptyList()
            }
        } else {
            return emptyList()
        }
    }

    companion object {
        private const val CANONICAL = "canonical"

        fun create(document: Document, strategies: List<(Link) -> (Document) -> LinkDefinition> =
        listOf(
                LinkDefinitionStrategies.ogTitle(),
                LinkDefinitionStrategies.ogDescription(),
                LinkDefinitionStrategies.starsCount(),
                LinkDefinitionStrategies.forksCount(),
                LinkDefinitionStrategies.latestCommitDate())
        ): (Link) -> List<LinkDefinition> {
            return DocumentLinkDefiner(document, strategies)
        }
    }
}