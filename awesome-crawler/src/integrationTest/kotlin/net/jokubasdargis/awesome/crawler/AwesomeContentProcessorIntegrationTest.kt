package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import org.junit.Test

class AwesomeContentProcessorIntegrationTest : BaseIntegrationTest() {

    @Test fun processAwesome() {
        val processor = AwesomeContentProcessor.create()
        val stream = documentStream("awesome.html")

        processor(stream, Link.from("https://github.com/sindresorhus/awesome"))

        stream.close()
    }

    @Test fun processKotlin() {
        val processor = AwesomeContentProcessor.create()
        val stream = documentStream("awesome-kotlin.html")

        processor(stream, Link.from("https://github.com/JavaBy/awesome-kotlin"))

        stream.close()
    }
}
