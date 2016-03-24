package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.DocumentDefinition
import net.jokubasdargis.awesome.core.LinkDefinition
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class AwesomeContentProcessorIntegrationTest(val documentResourcePath: String,
                                             val documentRootUrl: String,
                                             val numberOfLinks: Int,
                                             val numberOfLinkDefs: Int) : BaseIntegrationTest() {
    companion object {
        @Suppress("unused")
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf("awesome-rxjava.html",
                            "https://github.com/eleventigers/awesome-rxjava", 54, 148),
                    arrayOf("awesome.html",
                            "https://github.com/sindresorhus/awesome", 318, 675),
                    arrayOf("awesome-kotlin.html",
                            "https://github.com/JavaBy/awesome-kotlin", 152, 448),
                    arrayOf("awesome-linux-containers.html",
                            "https://github.com/Friz-zy/awesome-linux-containers", 87, 199),
                    arrayOf("awesome-ios.html",
                            "https://github.com/vsouza/awesome-ios", 1276, 3812)
            )
        }
    }

    @Test fun process() {
        val processor = AwesomeContentProcessor.create()
        val stream = documentStream(documentResourcePath)
        val link = Link.from(documentRootUrl) as Link.Identified
        val definitions = processor(stream, link)

        assertThat(definitions).hasSize(2)
        definitions.forEach {
            when (it) {
                is DocumentDefinition.Links -> assertThat(it()).hasSize(numberOfLinks)
                is DocumentDefinition.LinkDefinitions -> {
                    assertThat(it()).hasSize(numberOfLinkDefs)
                    it().filter { it is LinkDefinition.Relationship }
                            .forEach {
                                assertThat(link.equalHierarchy(it.link))
                            }
                }
            }
        }

        stream.close()
    }
}
