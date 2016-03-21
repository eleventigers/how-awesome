package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.util.Functions
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.io.InputStream

class AwesomeParsers private constructor() {
    companion object {

        private class SafeStreamDocumentParser<R>(
                val stream: InputStream,
                val factory: (Document) -> (Link) -> Iterable<R>) : (Link) -> Iterable<R> {

            override fun invoke(link: Link): Iterable<R> {
                val document = run {
                    try {
                        Jsoup.parse(stream, Charsets.UTF_8.name(), "") // consumes the stream
                    } catch (e: IOException) {
                        return emptyList()
                    }
                }
                return factory(document)(link)
            }
        }

        fun defineAwesomeDocument(stream: InputStream): (Link) -> Iterable<DocumentDefinition> {
            return SafeStreamDocumentParser(stream, { doc ->
                AwesomeDocumentDefiner.create(doc)
            })
        }

        fun extractAwesomeLinks(stream: InputStream): (Link) -> Iterable<Link> {
            return SafeStreamDocumentParser(stream, { doc ->
                val readme = AwesomeDocuments.readme(doc)
                if (readme != null) {
                    DefaultLinkExtractor.create(Html.links(readme))
                } else {
                    Functions.emptyLister<Link, Link>()
                }
            })
        }
    }
}
