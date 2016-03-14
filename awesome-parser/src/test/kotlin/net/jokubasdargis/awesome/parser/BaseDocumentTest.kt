package net.jokubasdargis.awesome.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

open class BaseDocumentTest {

    fun document(documentResourcePath: String): Document {
        val stream = javaClass.classLoader.getResourceAsStream(documentResourcePath)
        return Jsoup.parse(stream, Charsets.UTF_8.name(), "")
    }

    fun subElement(documentResourcePath: String, id: String): Element? {
        return document(documentResourcePath).getElementById(id)?.clone()
    }

    fun readmeElement(documentResourcePath: String): Element? {
        return subElement(documentResourcePath, "readme")
    }
}