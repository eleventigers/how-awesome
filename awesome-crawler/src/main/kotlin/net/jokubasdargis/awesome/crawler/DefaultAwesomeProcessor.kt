package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Host
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.asOrphans
import net.jokubasdargis.awesome.core.identified
import net.jokubasdargis.awesome.core.linkRelationships
import net.jokubasdargis.awesome.core.links
import net.jokubasdargis.awesome.core.ofHost
import net.jokubasdargis.awesome.parser.DocumentDescriber
import net.jokubasdargis.awesome.parser.DocumentDescribers
import net.jokubasdargis.awesome.util.Functions
import java.io.Closeable
import java.io.InputStream

internal class DefaultAwesomeProcessor private constructor(
        private val documentDescriber: DocumentDescriber) : Closeable, (Link) -> Boolean {

    override fun invoke(baseLink: Link): Boolean {
        if (baseLink !is Link.Identified) {
            return false
        }

        val descriptions = documentDescriber(baseLink)

        val allLinks = descriptions
                .links()
                .flatMap { it() }

        val github = allLinks
                .ofHost(Host.GITHUB)
                .asOrphans()

        val externalAwesome = github
                .filter(Functions.not(equalHierarchy(baseLink)))
                .filter(Functions.not(equalRepo(repoOrNull(baseLink))))

        val localAwesome = github
                .filter(equalRepo(repoOrNull(baseLink)))
                .filter { it.toUri() != baseLink.toUri() }

        val relationships = descriptions
                .linkRelationships()
                .flatMap { it() }


        localAwesome
                .flatMap { link ->
                    relationships
                            .identified()
                            .asOrphans()
                            .filter {
                                it.from() == link && !localAwesome.contains(it.to())
                            }
                }
                .forEach(::println)


        return true
    }

    override fun close() {
        documentDescriber.close()
    }

    companion object {
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

        fun create(stream: InputStream): DefaultAwesomeProcessor {
            return DefaultAwesomeProcessor(DocumentDescribers.forAwesomeReadme(stream))
        }
    }
}
