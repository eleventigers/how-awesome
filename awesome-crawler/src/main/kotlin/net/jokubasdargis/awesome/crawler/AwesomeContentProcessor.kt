package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.ContentType
import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDescription
import net.jokubasdargis.awesome.core.Relationship
import net.jokubasdargis.awesome.core.asOrphans
import net.jokubasdargis.awesome.core.identified
import net.jokubasdargis.awesome.core.linkDescriptions
import net.jokubasdargis.awesome.core.linkRelationships
import net.jokubasdargis.awesome.core.links
import net.jokubasdargis.awesome.core.ofHost
import net.jokubasdargis.awesome.parser.AwesomeParsers
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.ArrayList
import java.util.LinkedHashSet

internal class AwesomeContentProcessor private constructor(
        private val describerFactory: (InputStream) -> (Link) -> Iterable<DocumentDescription>) :
        ContentProcessor<Iterable<AwesomeDocumentDescription>> {

    override fun supportedContentTypes(): Set<ContentType> {
        return setOf(ContentTypes.html())
    }

    override fun invoke(stream: InputStream, baseLink: Link):
            Iterable<AwesomeDocumentDescription> {
        if (baseLink !is Link.Identified) {
            return emptyList()
        }

        val descriptions = describerFactory(stream)(baseLink)

        val allLinks = descriptions
                .links()
                .flatMap { it() }

        val githubLinks = allLinks
                .ofHost(Hosts.github())
                .asOrphans()

        val localLinks = githubLinks
                .filter(equalRepo(repoOrNull(baseLink)))
                .filter { it.toUri() != baseLink.toUri() }

        val relationships = descriptions
                .linkRelationships()
                .flatMap { it() }

        val linksToKeep = inRelationship(localLinks, relationships)

        val linkRelationshipsToKeep = relationships
                .identified()
                .asOrphans()
                .filter { linksToKeep.contains(it.from()) }

        val linkDescriptionsToKeep = descriptions
                .linkDescriptions()
                .flatMap { it() }
                .filter { it !is LinkDescription.None }
                .filter {
                    val link = it.link
                    if (link is Link.Identified) {
                        linksToKeep.contains(link.toOrphan())
                    } else {
                        false
                    }
                }

        val results: MutableList<AwesomeDocumentDescription> = ArrayList()
        if (linksToKeep.isNotEmpty()) {
            results.add(AwesomeDocumentDescription.Links(baseLink, linksToKeep))
        }
        if (linkRelationshipsToKeep.isNotEmpty()) {
            results.add(AwesomeDocumentDescription.LinkRelationships(baseLink,
                    linkRelationshipsToKeep.toSet()))
        }
        if (linkDescriptionsToKeep.isNotEmpty()) {
            results.add(AwesomeDocumentDescription.LinkDescriptions(baseLink,
                    linkDescriptionsToKeep.toSet()))
        }

        LOGGER.debug("Processed ${baseLink.canonicalize()}")

        return results.asIterable()
    }

    private fun inRelationship(links: Iterable<Link>,
                               relationships: Iterable<Relationship<Link>>): Set<Link.Identified> {
        return inRelationship(links, relationships, false)
                .plus(inRelationship(links, relationships, true))
                .fold(LinkedHashSet<Link.Identified>()) { acc, rel ->
                    acc.add(rel.from())
                    acc.add(rel.to())
                    acc
                }
    }

    private fun inRelationship(links: Iterable<Link>,
                               relationships: Iterable<Relationship<Link>>,
                               contain: Boolean): Iterable<Relationship<Link.Identified.Orphan>> {
        return links.flatMap { link ->
            relationships
                    .identified()
                    .asOrphans()
                    .filter {
                        it.from() == link && (contain == links.contains(it.to()))
                    }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AwesomeContentProcessor::class.java)

        private const val REPO_PATH_INDEX = 1;

        private fun repoOrNull(link: Link): String? {
            if (link is Link.Identified) {
                val segments = link.pathSegments()
                if (segments.size > REPO_PATH_INDEX) {
                    return segments[REPO_PATH_INDEX]
                } else {
                    return null
                }
            } else {
                return null
            }
        }

        private fun equalRepo(repo: String?): (Link) -> Boolean {
            return {
                when (it) {
                    is Link.Identified -> {
                        if (repo != null && repo == repoOrNull(it)) true else false
                    }
                    else -> false
                }
            }
        }

        fun create(describerFactory: (InputStream) -> (Link) -> Iterable<DocumentDescription> =
                   { AwesomeParsers.describeAwesomeReadme(it) }):
                ContentProcessor<Iterable<AwesomeDocumentDescription>> {
            return AwesomeContentProcessor(describerFactory)
        }
    }
}
