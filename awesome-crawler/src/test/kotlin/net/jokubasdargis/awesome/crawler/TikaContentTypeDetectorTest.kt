package net.jokubasdargis.awesome.crawler

import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.ContentType
import org.junit.Test
import java.io.ByteArrayInputStream
import java.math.BigInteger


class TikaContentTypeDetectorTest {

    companion object {
        private val SIGNATURE_GZ = BigInteger("1F8B", 16).toByteArray()
    }

    @Test fun nullStreamValidResourceName() {
        val sut = TikaContentTypeDetector.create()
        val type = sut(null, "index.html")

        assertThat(type).isEqualTo(ContentTypes.html())
    }

    @Test fun nullStreamBlankResourceName() {
        val sut = TikaContentTypeDetector.create()
        val type = sut(null, " ")

        assertThat(type).isNull()
    }

    @Test fun nullStreamUndetectableResourceName() {
        val sut = TikaContentTypeDetector.create()
        val type = sut(null, "foo")

        assertThat(type).isEqualTo(ContentTypes.octetStream())
    }

    @Test fun gzStreamNullResourceName() {
        val sut = TikaContentTypeDetector.create()
        val stream = ByteArrayInputStream(SIGNATURE_GZ)
        val type = sut(stream, null)

        assertThat(type).isEqualTo(ContentType.from("application/gzip"))
    }

    @Test fun emptyStreamValidResourceName() {
        val sut = TikaContentTypeDetector.create()
        val stream = ByteArrayInputStream(byteArrayOf())
        val type = sut(stream, "archive.tar")

        assertThat(type).isEqualTo(ContentType.from("application/x-tar"))
    }

    @Test fun getDetector() {
        val instance1 = TikaContentTypeDetector.get()
        val instance2 = TikaContentTypeDetector.get()

        assertThat(instance1).isEqualTo(instance2)
    }
}
