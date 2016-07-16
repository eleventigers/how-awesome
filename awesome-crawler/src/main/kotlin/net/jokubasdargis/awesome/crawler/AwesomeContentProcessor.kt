package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.Relationship
import net.jokubasdargis.awesome.core.identified
import net.jokubasdargis.awesome.core.ofHost
import net.jokubasdargis.awesome.parser.AwesomeParsers
import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.LinkDefinition
import net.jokubasdargis.awesome.core.linkDefinitions
import net.jokubasdargis.awesome.core.links
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.ArrayList
import java.util.LinkedHashSet

internal class AwesomeContentProcessor private constructor(
        private val definerFactory: (InputStream) -> (Link) -> Iterable<DocumentDefinition>) :
        ContentProcessor<Iterable<DocumentDefinition>> {

    override fun supportedContentTypes(): Set<ContentType> {
        return setOf(ContentTypes.html())
    }

    override fun invoke(stream: InputStream, baseLink: Link): Iterable<DocumentDefinition> {
        if (baseLink !is Link.Identified) {
            return emptyList()
        }

        val documentDefinitions = definerFactory(stream)(baseLink)

        val allLinks = documentDefinitions
                .links()
                .flatMap { it() }

        val githubLinks = allLinks
                .ofHost(Hosts.github())

        val localLinks = githubLinks
                .filter(equalRepo(repoOrNull(baseLink)))

        val linkRelationships = documentDefinitions
                .linkDefinitions()
                .flatMap { it() }
                .filter { it is LinkDefinition.Relationship }
                .map { it as LinkDefinition.Relationship }

        val linksToKeep = inRelationship(localLinks, linkRelationships)
                .toSet()

        val linkRelationshipsToKeep = linkRelationships
                .identified()
                .filter {
                    val from = it.from()
                    val to = it.to()
                    if (from == baseLink && !baseLink.equalHierarchy(to)) {
                        false
                    } else {
                        baseLink.equalHierarchy(from) && linksToKeep.contains(from)
                    }
                }
                .map { LinkDefinition.Relationship(it.from(), it.to()) }

        val nonRelationshipsToKeep = documentDefinitions
                .linkDefinitions()
                .flatMap { it() }
                .filter { it !is LinkDefinition.None }
                .filter { it !is LinkDefinition.Relationship }
                .filter {
                    val link = it.link
                    if (link is Link.Identified) {
                        linksToKeep.contains(link)
                    } else {
                        false
                    }
                }

        val linkDefinitionsToKeep = nonRelationshipsToKeep + linkRelationshipsToKeep

        val results: MutableList<DocumentDefinition> = ArrayList()
        if (linksToKeep.isNotEmpty()) {
            results.add(DocumentDefinition.Links(baseLink, linksToKeep))
        }
        if (linkDefinitionsToKeep.isNotEmpty()) {
            results.add(DocumentDefinition.LinkDefinitions(baseLink, linkDefinitionsToKeep.toSet()))
        }

        LOGGER.info("Processed ${baseLink.canonicalize()}")

        return results.asIterable()
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

        private fun inRelationship(
                links: Iterable<Link>,
                relationships: Iterable<Relationship<Link>>): Set<Link.Identified> {
            return inRelationship(links, relationships, false)
                    .plus(inRelationship(links, relationships, true))
                    .fold(LinkedHashSet<Link.Identified>()) { acc, rel ->
                        acc.add(rel.from())
                        acc.add(rel.to())
                        acc
                    }
        }

        @Suppress("UNCHECKED_CAST")
        private fun inRelationship(
                links: Iterable<Link>,
                relationships: Iterable<Relationship<Link>>,
                contain: Boolean): Iterable<Relationship<Link.Identified>> {
            return links.flatMap { link ->
                relationships
                        .identified()
                        .filter {
                            val from = it.from()
                            val to = it.to()
                            from == link && (contain == links.contains(to))
                        }
            }
        }

        fun create(definerFactory: (InputStream) -> (Link) -> Iterable<DocumentDefinition> =
                   { AwesomeParsers.defineAwesomeDocument(it) }):
                ContentProcessor<Iterable<DocumentDefinition>> {
            return AwesomeContentProcessor(definerFactory)
        }
    }

}
