package net.jokubasdargis.awesome.processor

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.io.InputStreamReader

open class BaseDocumentTest {

    fun readmeDocument(documentResourcePath: String) : Document? {
        val documentHtml = loadResourceAsString(documentResourcePath)
        val document = Jsoup.parse(documentHtml)
        val readme: Element? = document.getElementById("readme")
        val readmeHtml: String? = readme?.html()
        return if (readmeHtml != null) Jsoup.parse(readmeHtml) else null
    }

    fun loadResourceAsString(resName: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(resName);
        val reader: InputStreamReader? = stream.reader()
        val text = reader?.readText() ?: throw IOException("Could not read $resName")
        return text;
    }
}