package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import net.jokubasdargis.awesome.core.Relationship
import net.jokubasdargis.awesome.util.Functions
import org.jsoup.nodes.Document
import java.util.*

internal class AwesomeDocumentDefiner private constructor(
        private val readmeLinks: (Link) -> List<Link>,
        private val linkRelationshipFinder: (Link) -> List<Relationship<Link>>,
        private val inlineLinkDefiner: (Link) -> List<LinkDefinition>,
        private val documentLinkDefiner: (Link) -> List<LinkDefinition>) :
        (Link) -> List<DocumentDefinition> {

    override fun invoke(baseLink: Link): List<DocumentDefinition> {
        val links = readmeLinks(baseLink)
        val linksPlusBase = links.plus(baseLink)

        val documentLinkDefinitions = documentLinkDefiner(baseLink)

        val inlineLinkDefinitions = links
                .fold(ArrayList<LinkDefinition>()) { acc, v ->
                    inlineLinkDefiner(v).forEach { acc.add(it) }
                    acc
                }

        val relationships = links
//                .filter {
//                    if (it is Link.Identified && baseLink is Link.Identified) {
//                        it.canonicalize() == baseLink.canonicalize()
//                    } else {
//                        false
//                    }
//                }
                .map { LinkDefinition.Relationship(baseLink, it) }
                .plus(linkRelationshipFinder(baseLink)
                        .map { LinkDefinition.Relationship(it.from(), it.to()) })

        val linkDefinitions = documentLinkDefinitions
                .plus(inlineLinkDefinitions)
                .plus(relationships)

        return listOf(
                DocumentDefinition.Links(baseLink, linksPlusBase.toSet()),
                DocumentDefinition.LinkDefinitions(baseLink, linkDefinitions.toSet()))
    }

    companion object {
        fun create(document: Document): (Link) -> Iterable<DocumentDefinition> {
            val readme = AwesomeDocuments.readme(document) ?:
                    return Functions.emptyLister<Link, DocumentDefinition>()
            val readmeLinks = Functions.memoize(Html.links(readme))
            return AwesomeDocumentDefiner(
                    DefaultLinkExtractor.create(readmeLinks),
                    DefaultLinkRelationshipFinder.create(readmeLinks),
                    InlineLinkDefiner.create(readme),
                    DocumentLinkDefiner.create(document))
        }
    }
}
