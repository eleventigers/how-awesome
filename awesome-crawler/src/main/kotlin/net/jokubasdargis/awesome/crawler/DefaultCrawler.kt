package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.util.MarkableInputStream
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.time.Duration

internal class DefaultCrawler private constructor(
        private val linkFrontier: LinkFrontier,
        private val linkFetcher: (Link) -> Result<LinkResponse>,
        private val contentProcessors: Iterable<ContentProcessor<*>>,
        private val contentTypeDetector: (InputStream?, String?) -> ContentType?,
        private val contentFilter: (InputStream) -> Boolean) : Iterable<CrawlStats> {

    override fun iterator(): Iterator<CrawlStats> {
        return object : Iterator<CrawlStats> {

            override fun hasNext(): Boolean {
                return linkFrontier.hasNext()
            }

            override fun next(): CrawlStats {
                val start = System.currentTimeMillis()
                val crawlResult = crawl()
                val duration = System.currentTimeMillis() - start
                return CrawlStats(Duration.ofMillis(duration), crawlResult)
            }

            private fun crawl(): CrawlStatus {
                val link = linkFrontier.next()
                if (link !is Link.Identified) {
                    return CrawlStatus.Failure(CrawlException("Link is not identified: $link"))
                }
                val result = linkFetcher(link)
                if (result is Result.Success) {
                    val markStream = MarkableInputStream(result.value.stream)
                    try {
                        val readLimit = if (result.value.contentLength != -1L)
                            result.value.contentLength.toInt() else Int.MAX_VALUE
                        markStream.allowMarksToExpire(false)
                        val mark = markStream.savePosition(readLimit)
                        val accept = contentFilter(markStream)
                        markStream.reset(mark)
                        if (accept) {
                            val contentType = contentTypeDetector(markStream, link.canonicalize())
                            LOGGER.debug("Detected content type as '$contentType' for " +
                                    "${link.canonicalize()}")
                            val supported = if (contentType != null) contentProcessors
                                    .filter {
                                        it.supportedContentTypes().contains(contentType)
                                    } else emptyList()
                            if (!supported.isEmpty()) {
                                supported.forEach {
                                    markStream.reset()
                                    it(markStream, link)
                                }
                                markStream.allowMarksToExpire(true) // not sure
                            } else {
                                LOGGER.warn("No content processors found for " +
                                        "${link.canonicalize()} with $contentType")
                            }
                        } else {
                            LOGGER.info("Content previously seen at ${link.canonicalize()}")
                        }
                    } finally {
                        markStream.close()
                    }
                    return CrawlStatus.Success()
                } else {
                    LOGGER.warn("Failed to fetch ${link.canonicalize()}, error: $result")
                    val cause = if (result is Result.Failure) result.error else
                        Throwable("Unexpected link fetcher result")
                    return CrawlStatus.Failure(CrawlException(cause))
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultCrawler::class.java)
        private val AWESOME_ROOT = Link.from("https://github.com/sindresorhus/awesome")

        fun forAwesome(linkFrontier: LinkFrontier,
                       linkFetcher: (Link) -> Result<LinkResponse>): Iterable<CrawlStats> {
            val linkFilter = LinkFilters.combined(
                    LinkFilters.of(Hosts.github()),
                    LinkFilters.of(ContentTypes.html(), ContentTypes.octetStream()),
                    LinkFilters.lru(AWESOME_ROOT))
            val processors = setOf(
                    LinkFrontierAppendingContentProcessor
                            .create(AwesomeLinkExtractor.create(), linkFrontier, linkFilter),
                    AwesomeContentProcessor.create()
                            .withPersistor(AwesomeContentPersistor.create()))
            return create(linkFrontier, linkFetcher, processors)
        }

        fun create(linkFrontier: LinkFrontier,
                   linkFetcher: (Link) -> Result<LinkResponse>,
                   contentProcessors: Iterable<ContentProcessor<*>> = emptyList(),
                   contentTypeDetector: (InputStream?, String?) -> ContentType? =
                   TikaContentTypeDetector.get(),
                   contentFilter: (InputStream) -> Boolean = FingerPrintContentFilter.get()):
                Iterable<CrawlStats> {
            return DefaultCrawler(linkFrontier, linkFetcher, contentProcessors,
                    contentTypeDetector, contentFilter)
        }
    }
}
