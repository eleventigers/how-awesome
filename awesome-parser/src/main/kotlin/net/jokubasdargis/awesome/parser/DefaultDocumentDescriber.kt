package net.jokubasdargis.awesome.parser

import org.jsoup.nodes.Element
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
        fun create(element: Element): DefaultDocumentDescriber {
            return DefaultDocumentDescriber(
                    DefaultLinkFinder.create(element),
                    DefaultLinkDescriber.create(element))
        }
    }
}