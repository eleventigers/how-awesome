package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.parser.AwesomeParsers
import java.io.InputStream

internal class AwesomeLinkExtractor private constructor(
        private val extractorFactory: (InputStream) -> (Link) -> Iterable<Link>) :
        ContentProcessor<Iterable<Link>> {

    override fun supportedContentTypes(): Set<ContentType> {
        return setOf(ContentTypes.html())
    }

    override fun invoke(stream: InputStream, baseLink: Link): Iterable<Link> {
        if (baseLink !is Link.Identified) {
            return emptyList()
        }

        return extractorFactory(stream)(baseLink)
    }

    companion object {
        fun create(
                extractorFactory: (InputStream) -> (Link) -> Iterable<Link> =
                { stream -> AwesomeParsers.extractAwesomeLinks(stream) }):
                ContentProcessor<Iterable<Link>> {
            return AwesomeLinkExtractor(extractorFactory)
        }
    }
}
