package net.jokubasdargis.awesome.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal class AwesomeDocuments private constructor() {
    companion object {
        private const val ID_README = "readme"

        fun readme(document: Document): Element? {
            return document.getElementById(ID_README)?.clone()
        }
    }
}
