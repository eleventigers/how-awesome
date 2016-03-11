package net.jokubasdargis.awesome.processor

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class LinkFinderTest(val documentResourcePath: String, val documentRootUrl: String,
                     val numberOfLinks: Int) : BaseDocumentTest() {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Array<Any>> {
            return listOf(
                    arrayOf("awesome.html","https://github.com/sindresorhus/awesome", 318),
                    arrayOf("awesome-kotlin.html", "https://github.com/JavaBy/awesome-kotlin", 151),
                    arrayOf("awesome-java.html", "https://github.com/akullpp/awesome-java", 457),
                    arrayOf("awesome-dart.html", "https://github.com/yissachar/awesome-dart", 87),
                    arrayOf("awesome-osx-command-line.html",
                            "https://github.com/herrbischoff/awesome-osx-command-line", 299)

            )
        }
    }

    @Test fun findLinks() {
        val linkFinder = LinkFinder(Link.from(documentRootUrl))
        val linkList = linkFinder.find(readmeDocument(documentResourcePath)!!)

        linkList.links().forEach(::println)

        assertThat(linkList.links().size).isEqualTo(numberOfLinks)
    }
}
