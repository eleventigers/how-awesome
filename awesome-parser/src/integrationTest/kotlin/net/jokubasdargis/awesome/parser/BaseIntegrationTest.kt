package net.jokubasdargis.awesome.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.InputStream

open class BaseIntegrationTest {
    fun documentStream(documentResourcePath: String): InputStream {
        return javaClass.classLoader.getResourceAsStream(documentResourcePath)
    }

    fun document(documentResourcePath: String): Document {
        return Jsoup.parse(documentStream(documentResourcePath), Charsets.UTF_8.name(), "")
    }

    fun subElement(documentResourcePath: String, id: String): Element? {
        return document(documentResourcePath).getElementById(id)?.clone()
    }

    fun readmeElement(documentResourcePath: String): Element? {
        return subElement(documentResourcePath, "readme")
    }
}
