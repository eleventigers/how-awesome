package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DefaultCrawlerIntegrationTest : BaseIntegrationTest() {

    companion object {
        private val LINK_AWESOME = Link.from("https://github.com/sindresorhus/awesome")
    }

    @JvmField @Rule val temporaryFolder = TemporaryFolder()

    @Test fun inMemoryLinkFrontier() {
        val fetcher = OkHttpLinkFetcher.create()
        val linkFrontier = InMemoryQueueLinkFrontier.create()
        linkFrontier.add(LINK_AWESOME)

        val count = 1

        val executor = Executors.newFixedThreadPool(count)

        val tasks: List<Callable<Unit>> = 0.rangeTo(count).map {
            Callable<kotlin.Unit> {
                val crawler = DefaultCrawler.forAwesome(linkFrontier, fetcher)
                val iterator = crawler.iterator()
                while (true) {
                    iterator.next()
                }
            }
        }

        executor.invokeAll(tasks, 500, TimeUnit.SECONDS)
    }

    @Test fun fileQueueLinkFrontier() {
        val fetcher = OkHttpLinkFetcher.create()
        val file = File(temporaryFolder.getRoot(), "frontier")
        val linkFrontier = FileQueueLinkFrontier.create(file)
        linkFrontier.add(LINK_AWESOME)

        val count = 1
        val executor = Executors.newFixedThreadPool(count)

        val tasks: List<Callable<Unit>> = 0.rangeTo(count).map {
            Callable<kotlin.Unit> {
                val crawler = DefaultCrawler.forAwesome(linkFrontier, fetcher)
                val iterator = crawler.iterator()
                while (true) {
                    iterator.next()
                }
            }
        }

        executor.invokeAll(tasks, 5, TimeUnit.SECONDS)
    }

    @Test fun resourceLinkFetcher() {
        val linkFrontier = InMemoryQueueLinkFrontier.create()
        linkFrontier.add(LINK_AWESOME)

        val fetcher: (Link) -> Result<LinkResponse> = {
            Result.Success(LinkResponse(documentStream("awesome.html"), -1))
        }
        val sut = DefaultCrawler.forAwesome(linkFrontier, fetcher)

        sut.forEach {
            //ignored
        }
    }
}