package net.jokubasdargis.awesome.processor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.io.InputStreamReader

open class BaseDocumentTest {

    fun document(documentResourcePath: String): Document {
        val documentHtml = loadResourceAsString(documentResourcePath)
        return Jsoup.parse(documentHtml)
    }

    fun subDocument(documentResourcePath: String, id: String): Document? {
        val document = document(documentResourcePath)
        val sub: Element? = document.getElementById(id)
        val subHtml: String? = sub?.html()
        return if (subHtml != null) Jsoup.parse(subHtml) else null
    }

    fun readmeDocument(documentResourcePath: String): Document? {
        return subDocument(documentResourcePath, "readme")
    }

    fun loadResourceAsString(resName: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(resName);
        val reader: InputStreamReader? = stream.reader()
        val text = reader?.readText() ?: throw IOException("Could not read $resName")
        return text;
    }
}