package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class LinkFrontierAppendingContentProcessor(
        val linkExtractor: ContentProcessor<Iterable<Link>>,
        val linkFrontier: LinkFrontier,
        val linkFilter: (Link) -> Boolean,
        val executorService: ExecutorService) : ContentProcessor<Unit> {

    override fun supportedContentTypes(): Set<ContentType> {
        return linkExtractor.supportedContentTypes()
    }

    override fun invoke(stream: InputStream, link: Link) {
        val links = linkExtractor(stream, link)
        executorService.submit {
            var count = 0
            val accepted = links
                    .map {
                        count++
                        it
                    }
                    .filter(linkFilter)
                    .map { linkFrontier.add(it) }

            LOGGER.info("Added ${accepted.size}/$count links to the $linkFrontier")
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(
                LinkFrontierAppendingContentProcessor::class.java)

        fun create(linkExtractor: ContentProcessor<Iterable<Link>>,
                   linkFrontier: LinkFrontier,
                   linkFilter: (Link) -> Boolean,
                   executorService: ExecutorService = Executors.newSingleThreadExecutor()):
                ContentProcessor<Unit> {
            return LinkFrontierAppendingContentProcessor(
                    linkExtractor, linkFrontier, linkFilter, executorService)
        }
    }
}
