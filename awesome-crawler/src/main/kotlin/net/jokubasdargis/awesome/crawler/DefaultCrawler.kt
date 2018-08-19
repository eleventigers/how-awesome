package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.Result
import net.jokubasdargis.awesome.util.MarkableInputStream
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.time.Duration

internal class DefaultCrawler private constructor(
        private val linkFrontier: LinkFrontier,
        private val linkFetcher: (Link) -> Result<LinkResponse>,
        private val contentProcessors: Iterable<ContentProcessor<*>>,
        private val contentTypeDetector: (InputStream?, String?) -> ContentType?,
        private val contentFilter: (InputStream) -> Boolean) : Crawler {

    override fun iterator(): MutableIterator<CrawlStats> {
        return object : MutableIterator<CrawlStats> {

            override fun hasNext(): Boolean {
                return !linkFrontier.isEmpty()
            }

            override fun remove() {
                linkFrontier.remove()
            }

            override fun next(): CrawlStats {
                val start = System.nanoTime()
                val result = crawl()
                val duration = System.nanoTime() - start
                val crawlStat = CrawlStats(Duration.ofNanos(duration), result)
                LOGGER.info("Crawl completed with {}", crawlStat)
                return crawlStat
            }

            private fun crawl(): Result<Link> {
                val link = linkFrontier.peek()
                if (link !is Link.Identified) {
                    return Result.Failure(CrawlException(link, "Link is not identified: $link"))
                }
                val result = linkFetcher(link)
                if (result is Result.Success) {
                    MarkableInputStream(result.value.stream).use { markStream ->
                        val readLimit = if (result.value.contentLength != -1L)
                            result.value.contentLength.toInt() else Int.MAX_VALUE
                        markStream.allowMarksToExpire(false)
                        val mark = markStream.savePosition(readLimit)
                        val accept = contentFilter(markStream)
                        markStream.reset(mark)
                        if (accept) {
                            val contentType = contentTypeDetector(markStream, link.canonicalize())
                            LOGGER.info("Detected content type as '$contentType' for " +
                                    link.canonicalize())
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
                    }
                    return Result.Success(link)
                } else {
                    LOGGER.warn("Failed to fetch ${link.canonicalize()}, error: $result")
                    val cause = (result as? Result.Failure)
                            ?.error ?: Throwable("Unexpected link fetcher result")
                    return Result.Failure(CrawlException(link, cause))
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DefaultCrawler::class.java)

        fun create(linkFrontier: LinkFrontier,
                   linkFetcher: (Link) -> Result<LinkResponse>,
                   contentProcessors: Iterable<ContentProcessor<*>> = emptyList(),
                   contentTypeDetector: (InputStream?, String?) -> ContentType? =
                   TikaContentTypeDetector.get(),
                   contentFilter: (InputStream) -> Boolean = FingerPrintContentFilter.get()):
                Crawler {
            return DefaultCrawler(linkFrontier, linkFetcher, contentProcessors,
                    contentTypeDetector, contentFilter)
        }
    }
}
