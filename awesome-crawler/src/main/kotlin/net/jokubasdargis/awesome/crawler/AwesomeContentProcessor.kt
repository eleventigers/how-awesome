package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.ContentType
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
import java.util.HashSet

internal class AwesomeContentProcessor private constructor() : ContentProcessor<Unit> {

    override fun supportedContentTypes(): Set<ContentType> {
        return setOf(ContentTypes.html())
    }

    override fun invoke(stream: InputStream, baseLink: Link) {
        if (baseLink !is Link.Identified) {
            return
        }

        val descriptions = AwesomeParsers.describeAwesomeReadme(stream)(baseLink)

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

        val linkDescriptions = descriptions
                .linkDescriptions()
                .flatMap { it() }

        val localToExternal = inRelationship(localLinks, relationships, false)
        val localToLocal = inRelationship(localLinks, relationships, true)

        val linksToKeep = localToExternal.plus(localToLocal)
                .fold(HashSet<Link.Identified>()) { acc, rel ->
                    acc.add(rel.from())
                    acc.add(rel.to())
                    acc
                }

        val linkRelationshipsToKeep = relationships
                .identified()
                .asOrphans()
                .filter { linksToKeep.contains(it.from()) }

        val linkDescriptionsToKeep = linkDescriptions
                .filter { it !is LinkDescription.None }
                .filter {
                    val link = it.link
                    if (link is Link.Identified) {
                        linksToKeep.contains(link.toOrphan())
                    } else {
                        false
                    }
                }

        LOGGER.debug("Processed ${baseLink.canonicalize()}")
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

        private fun equalHierarchy(link: Link): (Link) -> Boolean {
            return {
                when (it) {
                    is Link.Identified -> {
                        it.equalHierarchy(link)
                    }
                    else -> false
                }
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

        fun create(): ContentProcessor<Unit> {
            return AwesomeContentProcessor()
        }
    }
}
