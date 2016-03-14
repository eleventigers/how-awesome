package net.jokubasdargis.awesome.processor

data class DocumentSummary(private val documentLink: Link,
                           private val links: List<Link>,
                           private val linkSummaries: List<LinkSummary>,
                           private val linkRelationships: List<Relationship<Link>>) {

    fun links(): List<Link> {
        return links
    }

    fun linksOfDocument(): List<Link> {
        return links.filter { it.equalHierarchy(documentLink) }
    }

    fun linksOfHost(host: Host): List<Link> {
        return links.filter { it.ofHost(host) }
    }

    fun linksNotOfHost(host: Host): Set<Link> {
        return links.subtract(linksOfHost(host))
    }

    companion object {
        fun create(documentLink: Link, links: List<Link>, linkSummaries: List<LinkSummary>,
                   linkRelationships: List<Relationship<Link>>): DocumentSummary {
            return DocumentSummary(documentLink, links, linkSummaries, linkRelationships)
        }
    }
}