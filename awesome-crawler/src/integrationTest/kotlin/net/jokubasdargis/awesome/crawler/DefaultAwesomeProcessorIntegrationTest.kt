package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.junit.Test

class DefaultAwesomeProcessorIntegrationTest : BaseIntegrationTest() {

    @Test fun processAwesome() {
        val result = DefaultAwesomeProcessor.create(documentStream("awesome.html")).use {
            it(Link.from("https://github.com/sindresorhus/awesome"))
        }

        assertThat(result).isTrue()
    }

    @Test fun processAwesomeDart() {
        val result = DefaultAwesomeProcessor.create(documentStream("awesome-dart.html")).use {
            it(Link.from("https://github.com/yissachar/awesome-dart"))
        }

        assertThat(result).isTrue()
    }

    @Test fun processAwesomeKotlin() {
        val result = DefaultAwesomeProcessor.create(documentStream("awesome-kotlin.html")).use {
            it(Link.from("https://github.com/JavaBy/awesome-kotlin"))
        }

        assertThat(result).isTrue()
    }
}
