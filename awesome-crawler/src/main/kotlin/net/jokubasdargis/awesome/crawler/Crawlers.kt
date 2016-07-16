package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.Result
import net.jokubasdargis.awesome.message.MessageRouter
import net.jokubasdargis.awesome.message.MessageRouters

class Crawlers private constructor() {
    companion object {
        private val AWESOME_ROOT = Link.from("https://github.com/sindresorhus/awesome")

        @JvmStatic fun awesomeRootLink(): Link {
            return AWESOME_ROOT
        }

        @JvmStatic @JvmOverloads fun newAwesomeCrawler(
                linkFrontier: LinkFrontier,
                linkFetcher: (Link) -> Result<LinkResponse> = OkHttpLinkFetcher.create(),
                messageRouter: MessageRouter = MessageRouters.noop()): Crawler {
            val linkFilter = LinkFilters.combined(
                    LinkFilters.of(Hosts.github()),
                    LinkFilters.of(ContentTypes.html(), ContentTypes.octetStream()),
                    LinkFilters.lru(awesomeRootLink()))
            val processors = setOf(
                    LinkFrontierAppendingContentProcessor
                            .create(AwesomeLinkExtractor.create(), linkFrontier, linkFilter),
                    AwesomeContentProcessor.create()
                            .withPersistor(AwesomeContentPersistor.create(messageRouter)))
            return DefaultCrawler.create(linkFrontier, linkFetcher, processors)
        }
    }
}