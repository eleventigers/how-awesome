package net.jokubasdargis.awesome.crawler

import java.io.InputStream

open class BaseIntegrationTest {
    fun documentStream(documentResourcePath: String): InputStream {
        return BaseIntegrationTest::class.java.classLoader.getResourceAsStream(documentResourcePath)
    }
}
