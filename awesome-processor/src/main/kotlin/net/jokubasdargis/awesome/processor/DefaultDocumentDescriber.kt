package net.jokubasdargis.awesome.processor

import org.jsoup.nodes.Document
import java.util.HashMap

internal class DefaultDocumentDescriber private constructor(
        private val linkFinder: LinkFinder,
        private val linkDescriber: LinkDescriber) : DocumentDescriber {

    override fun describe(value: Link): List<DocumentDescription> {
        val linkList = linkFinder.find(value)
        val linkDescriptions = linkList.links()
                .fold(HashMap<Link, List<LinkDescription>>()) { acc, v ->
                    acc.put(v, linkDescriber.describe(v))
                    acc
                }

        return listOf(
                DocumentDescription.Links(linkList.links()),
                DocumentDescription.LinkDescriptions(linkDescriptions),
                DocumentDescription.LinkRelationships(linkList.relationships()))
    }

    companion object {
        fun create(document: Document): DefaultDocumentDescriber {
            return DefaultDocumentDescriber(
                    DefaultLinkFinder.create(document),
                    DefaultLinkDescriber.create(document))
        }
    }
}