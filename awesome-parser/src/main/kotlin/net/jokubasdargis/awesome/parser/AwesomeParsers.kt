package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Link
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.InputStream

class AwesomeParsers private constructor() {
    companion object {

        private const val ID_README = "readme"

        private fun <R> noopParser(): (Link) -> Iterable<R> {
            return {
                emptyList()
            }
        }

        private class ReadmeDocumentParser<R>(
                val stream: InputStream,
                val factory: (Element) -> (Link) -> Iterable<R>) : (Link) -> Iterable<R> {

            override fun invoke(link: Link): Iterable<R>  {
                val document = Jsoup.parse(stream, Charsets.UTF_8.name(), "") // consumes the stream
                val readme = document.getElementById(ID_README)?.clone()
                val describer = if (readme != null) factory(readme) else noopParser<R>()
                return describer.invoke(link)
            }
        }

        fun describeAwesomeReadme(stream: InputStream): (Link) -> Iterable<DocumentDescription> {
            return ReadmeDocumentParser(stream, { el ->
                DefaultDocumentDescriber.create(el)
            })
        }

        fun extractAwesomeReadmeLinks(stream: InputStream): (Link) -> Iterable<Link> {
            return ReadmeDocumentParser(stream, { el ->
                DefaultLinkExtractor.create(Html.links(el))
            })
        }
    }
}
