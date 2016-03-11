package net.jokubasdargis.awesome.processor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.IOException
import java.io.InputStreamReader


@RunWith(Parameterized::class)
class LinkFinderTest(val documentResourcePath: String, val documentRootUrl: String,
                     val numberOfLinks: Int) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Array<Any>> {
            return listOf(
                    arrayOf("awesome.html","https://github.com/sindresorhus/awesome", 318),
                    arrayOf("awesome-kotlin.html", "https://github.com/JavaBy/awesome-kotlin", 151),
                    arrayOf("awesome-java.html", "https://github.com/akullpp/awesome-java", 457),
                    arrayOf("awesome-dart.html", "https://github.com/yissachar/awesome-dart", 86),
                    arrayOf("awesome-osx-command-line.html",
                            "https://github.com/herrbischoff/awesome-osx-command-line", 299)

            )
        }
    }

    @Test fun findLinks() {
        val linkFinder = createFinder(documentResourcePath, documentRootUrl)
        val linkList = linkFinder.find()

        assertThat(linkList.links().size).isEqualTo(numberOfLinks)
    }

    private fun createFinder(documentResourcePath: String, documentRootUrl: String) : LinkFinder {
        val documentHtml = loadResourceAsString(documentResourcePath)
        val document = Jsoup.parse(documentHtml)
        val readme: Element? = document.getElementById("readme")
        val readmeHtml: String? = readme?.html()
        val readmeDocument: Document? = if (readmeHtml != null) Jsoup.parse(readmeHtml) else null
        val elements: List<Element> = readmeDocument?.select("a[href]") ?: emptyList<Element>()

        return LinkFinder(elements, Link.from(documentRootUrl))
    }

    private fun loadResourceAsString(resName: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(resName);
        val reader: InputStreamReader? = stream.reader()
        val text = reader?.readText() ?: throw IOException("Could not read $resName")
        return text;
    }
}
