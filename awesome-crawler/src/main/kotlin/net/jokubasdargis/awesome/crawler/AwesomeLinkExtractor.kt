package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.ContentType
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.parser.AwesomeParsers
import java.io.InputStream

internal class AwesomeLinkExtractor private constructor() : ContentProcessor<Iterable<Link>> {

    override fun supportedContentTypes(): Set<ContentType> {
        return setOf(ContentTypes.html())
    }

    override fun invoke(stream: InputStream, baseLink: Link): Iterable<Link> {
        if (baseLink !is Link.Identified) {
            return emptyList()
        }

        val extractor = AwesomeParsers.extractAwesomeReadmeLinks(stream)
        val links = extractor(baseLink)

        return links
    }

    companion object {
        fun create(): ContentProcessor<Iterable<Link>> {
            return AwesomeLinkExtractor()
        }
    }
}
