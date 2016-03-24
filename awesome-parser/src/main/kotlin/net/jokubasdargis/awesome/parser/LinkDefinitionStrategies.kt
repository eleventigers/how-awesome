package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.text.Normalizer
import java.text.NumberFormat
import java.text.ParseException
import java.time.Instant
import java.util.Locale

internal class LinkDefinitionStrategies {
    companion object {
        private val NONE_STRATEGY: (Link) -> (Element) -> LinkDefinition = { link ->
            {
                LinkDefinition.None(link)
            }
        }

        private val INLINE_TITLE_STRATEGY: (Link) -> (Element) -> LinkDefinition = { link ->
            {
                var title = it.text()
                if (title.isNullOrBlank()) {
                    title = findTextInSiblings(it)
                }

                title = clean(title)
                if (title != null) {
                    LinkDefinition.Title(link, title)
                } else {
                    LinkDefinition.None(link)
                }
            }
        }

        private val INLINE_DESCRIPTION_STRATEGY: (Link) -> (Element) -> LinkDefinition = { link ->
            {
                var summary = clean(findTextInSiblings(it))
                if (summary != null) {
                    LinkDefinition.Description(link, summary)
                } else {
                    LinkDefinition.None(link)
                }
            }

        }
        private val PROP_OG_TITLE = "og:title"
        private val PROP_OG_DESCRIPTION = "og:description"

        private val OG_TITLE_STRATEGY: (Link) -> (Document) -> LinkDefinition = { link ->
            {
                val ogTitle = it.head()
                        ?.getElementsByAttributeValue(Html.Attr.PROPERTY.value, PROP_OG_TITLE)
                        ?.firstOrNull()

                var title = ogTitle?.attr(Html.Attr.CONTENT.value)

                title = clean(title)
                if (title != null) {
                    LinkDefinition.Title(link, title)
                } else {
                    LinkDefinition.None(link)
                }
            }
        }

        private val OG_DESCRIPTION_STRATEGY: (Link) -> (Document) -> LinkDefinition = { link ->
            {
                val ogDescription = it.head()
                        ?.getElementsByAttributeValue(Html.Attr.PROPERTY.value, PROP_OG_DESCRIPTION)
                        ?.firstOrNull()
                var description = ogDescription?.attr(Html.Attr.CONTENT.value)

                description = if (link is Link.Identified) {
                    val last = link.pathSegments().lastOrNull()
                    if (last != null) {
                        description?.removePrefix(last) // remove repo name if prefixed
                    } else {
                        description
                    }
                } else description

                description = clean(description, RELAXED_CHARS)
                if (description != null) {
                    LinkDefinition.Description(link, description)
                } else {
                    LinkDefinition.None(link)
                }
            }
        }

        private val NUMBER_FORMAT = NumberFormat.getInstance(Locale.ENGLISH)
        private val SEGMENT_STARGAZERS = "stargazers"
        private val SEGMENT_NETWORK = "network"
        private val PROP_DATE_MODIFIED = "dateModified"

        private val STARS_COUNT_STRATEGY: (Link) -> (Document) -> LinkDefinition = { link ->
            {
                var definition : LinkDefinition = LinkDefinition.None(link)

                val starsLink = if (link is Link.Identified) {
                    link.path() + "/" + SEGMENT_STARGAZERS
                } else null

                if (starsLink != null) {
                    val stargazers = it
                            .getElementsByAttributeValueContaining(Html.Attr.HREF.value, starsLink)
                            .firstOrNull()
                    if (stargazers != null) {
                        try {
                            val count = NUMBER_FORMAT.parse(stargazers.text()).toInt()
                            definition = LinkDefinition.StarsCount(link, count)
                        } catch (e: ParseException) {
                            // ignored
                            -1
                        }
                    }
                }

                definition
            }
        }

        private val FORKS_COUNT_STRATEGY: (Link) -> (Document) -> LinkDefinition = { link ->
            {
                var definition : LinkDefinition = LinkDefinition.None(link)

                val starsLink = if (link is Link.Identified) {
                    link.toUri().path + "/" + SEGMENT_NETWORK
                } else null

                if (starsLink != null) {
                    val stargazers = it
                            .getElementsByAttributeValueContaining(Html.Attr.HREF.value, starsLink)
                            .firstOrNull()
                    if (stargazers != null) {
                        try {
                            val count = NUMBER_FORMAT.parse(stargazers.text()).toInt()
                            definition = LinkDefinition.ForksCount(link, count)
                        } catch (e: ParseException) {
                            // ignored
                            -1
                        }
                    }
                }

                definition
            }
        }

        private val LATEST_COMMIT_STRATEGY: (Link) -> (Document) -> LinkDefinition = { link ->
            {
                var definition : LinkDefinition = LinkDefinition.None(link)

                val times = it.getElementsByTag(Html.Tag.TIME.value)
                if (times.isNotEmpty()) {
                    val time = times.filter {
                        it.parent()?.attr(Html.Attr.ITEMPROP.value) == PROP_DATE_MODIFIED
                    }
                    .firstOrNull()

                    if (time != null) {
                        val date = Instant.parse(time.attr(Html.Attr.DATETIME.value))
                        if (date != null) {
                            definition = LinkDefinition.LatestCommitDate(link, date)
                        }
                    }
                }

                definition
            }
        }

        private fun findTextInSiblings(element: Element): String? {
            var text: String? = null

            val siblingNode = element.nextSibling()
            if (siblingNode is TextNode) {
                text = siblingNode.text()
            }

            if (text.isNullOrBlank()) {
                val siblingElement = element.nextElementSibling();
                if (!Html.Tag.lists().contains(Html.Tag.from(siblingElement?.tagName()))) {
                    text = siblingElement?.text()
                }
            }

            return text
        }

        private val BASIC_CHARS = "^[^a-zA-z0-9()]*|[^a-zA-Z0-9()]*$".toRegex()
        private val RELAXED_CHARS = "^[^a-zA-z0-9():]*|[^a-zA-Z0-9():]*$".toRegex()

        private fun clean(value: String?): String? {
           return clean(value, BASIC_CHARS)
        }

        private fun clean(value: String?, regex: Regex): String? {
            if (!value.isNullOrBlank()) {
                val normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
                val cleaned = normalized.replace(regex, "")
                return if (cleaned.isBlank()) null else cleaned
            } else {
                return null
            }
        }

        fun inlineTitle(): (Link) -> (Element) -> LinkDefinition {
            return INLINE_TITLE_STRATEGY
        }

        fun inlineDescription(): (Link) -> (Element) -> LinkDefinition {
            return INLINE_DESCRIPTION_STRATEGY
        }

        fun none(): (Link) -> (Element) -> LinkDefinition {
            return NONE_STRATEGY
        }

        fun ogTitle(): (Link) -> (Document) -> LinkDefinition {
            return OG_TITLE_STRATEGY
        }

        fun ogDescription(): (Link) -> (Document) -> LinkDefinition {
            return OG_DESCRIPTION_STRATEGY
        }

        fun starsCount(): (Link) -> (Document) -> LinkDefinition {
            return STARS_COUNT_STRATEGY
        }

        fun forksCount(): (Link) -> (Document) -> LinkDefinition {
            return FORKS_COUNT_STRATEGY
        }

        fun latestCommitDate():  (Link) -> (Document) -> LinkDefinition {
            return LATEST_COMMIT_STRATEGY
        }
    }
}
