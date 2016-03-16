package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDescription
import net.jokubasdargis.awesome.core.Relationship
import net.jokubasdargis.awesome.util.Functions
import org.jsoup.nodes.Element
import java.util.HashMap

internal class DefaultDocumentDescriber private constructor(
        private val linkExtractor: (Link) -> List<Link>,
        private val linkRelationshipFinder: (Link) -> List<Relationship<Link>>,
        private val linkDescriber: (Link) -> List<LinkDescription>) :
        (Link) -> List<DocumentDescription> {

    override fun invoke(value: Link): List<DocumentDescription> {
        val links = linkExtractor(value)
        val linkRelationships = linkRelationshipFinder(value)
        val linkDescriptions = links
                .fold(HashMap<Link, List<LinkDescription>>()) { acc, v ->
                    acc.put(v, linkDescriber(v))
                    acc
                }

        return listOf(
                DocumentDescription.Links(links),
                DocumentDescription.LinkRelationships(linkRelationships),
                DocumentDescription.LinkDescriptions(linkDescriptions))
    }

    companion object {
        fun create(element: Element): (Link) -> List<DocumentDescription> {
            val linkElements = Functions.memoize(Html.links(element))
            return DefaultDocumentDescriber(
                    DefaultLinkExtractor.create(linkElements),
                    DefaultLinkRelationshipFinder.create(linkElements),
                    DefaultLinkDescriber.create(element))
        }
    }
}
