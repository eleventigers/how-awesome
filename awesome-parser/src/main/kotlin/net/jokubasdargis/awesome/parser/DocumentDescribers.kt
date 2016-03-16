package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Link
import org.jsoup.Jsoup
import java.io.InputStream

class DocumentDescribers {
    companion object {

        private const val ID_README = "readme"

        private val NOOP_DESCRIBER: (Link) -> List<DocumentDescription> = {
            emptyList()
        }

        private class ClosableDocumentDescriber(
                val stream: InputStream,
                val describer: (Link) -> List<DocumentDescription>) : DocumentDescriber {
            override fun close() {
                stream.close()
            }

            override fun invoke(link: Link): List<DocumentDescription> {
                return describer(link)
            }
        }

        fun forAwesomeReadme(stream: InputStream): DocumentDescriber {
            val document = Jsoup.parse(stream, Charsets.UTF_8.name(), "")
            val readme = document.getElementById(ID_README)?.clone()
            val describer = if (readme != null)
                DefaultDocumentDescriber.create(readme) else NOOP_DESCRIBER
            return ClosableDocumentDescriber(stream, describer)
        }
    }
}
