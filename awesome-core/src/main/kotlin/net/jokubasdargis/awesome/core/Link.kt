package net.jokubasdargis.awesome.core

import io.mola.galimatias.URL
import io.mola.galimatias.canonicalize.CombinedCanonicalizer
import io.mola.galimatias.canonicalize.RFC3986Canonicalizer
import io.mola.galimatias.canonicalize.StripPartCanonicalizer
import java.net.URI

sealed class Link private constructor(val raw: String) {

    sealed class Identified private constructor(protected val url: URL, raw: String) : Link(raw) {

        fun toOrphan(): Orphan {
            return when (this) {
                is Orphan -> this
                else -> Orphan(url, raw)
            }
        }

        class Orphan(url: URL, raw: String) : Identified(url, raw) {

            override fun toString(): String {
                return "Orphan(url=$url)"
            }
        }

        class Parented(url: URL, raw: String, val parent: Link) : Identified(url, raw) {

            override fun equals(other: Any?): Boolean {
                if (this === other) {
                    return true
                }
                if (other !is Parented) {
                    return false
                }
                if (!super.equals(other)) {
                    return false
                }

                if (parent != other.parent) {
                    return false
                }

                return true
            }

            override fun hashCode(): Int {
                var result = super.hashCode()
                result += 31 * result + parent.hashCode()
                return result
            }

            override fun toString(): String {
                return "Parented(url=$url, parent=$parent)"
            }
        }

        fun toUri(): URI {
            return url.toJavaURI()
        }

        fun toUrl(): java.net.URL {
            return url.toJavaURL()
        }

        fun pathSegments(): List<String> {
            return url.pathSegments() ?: emptyList()
        }

        fun host(): Host? {
            return Host.from(url.host()?.toString())
        }

        fun ofHost(host: Host): Boolean {
            return host.apply(url.host()?.toString())
        }

        fun equalHierarchy(other: Link): Boolean {
            when (other) {
                is Identified -> {
                    if (!url.isHierarchical || !other.url.isHierarchical) {
                        return false
                    }
                    if (!url.authority().equals(other.url.authority())) {
                        return false
                    }
                    if (!url.path().equals(other.url.path())) {
                        return false
                    }
                    return true
                }
                is Invalid -> {
                    return false
                }
                else -> return false
            }
        }

        fun canonicalize(): String {
            return CANONICALIZER.canonicalize(url).toString()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Identified) {
                return false
            }

            if (url != other.url) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return url.hashCode()
        }
    }

    class Invalid internal constructor(raw: String) : Link(raw) {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Link.Invalid) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return 0
        }
    }

    companion object {
        private val CANONICALIZER = CombinedCanonicalizer(
                StripPartCanonicalizer(StripPartCanonicalizer.Part.FRAGMENT),
                RFC3986Canonicalizer())

        fun from(string: String, parent: Link? = null): Link {
            try {
                val url = URL.fromJavaURI(resolve(URI(string), parent))
                if (parent != null) {
                    return Link.Identified.Parented(url, string, parent)
                } else {
                    return Link.Identified.Orphan(url, string)
                }
            } catch (e: Exception) {
                return Link.Invalid(string)
            }
        }

        private fun resolve(uri: URI, parent: Link?): URI {
            return if (uri.host != null) uri else {
                when (parent) {
                    is Identified -> {
                        //TODO(eleventigers, 14.03.16): resolve scheme relative (//path) uris
                        return parent.toUri().resolve(uri)
                    }
                    is Invalid -> {
                        return uri
                    }
                    else -> uri
                }
            }
        }
    }
}

fun <T : Iterable<Link>> T.ofHost(host: Host): Iterable<Link.Identified> {
    return filter {
        when (it) {
            is Link.Identified -> {
                it.ofHost(host)
            }
            else -> false
        }
    }.map { it as Link.Identified }
}

fun <T : Iterable<Link>> T.identified(): Iterable<Link.Identified> {
    return filter{ it is Link.Identified }.map { it as Link.Identified }
}

fun <T : Iterable<Link.Identified>> T.asOrphans(): Iterable<Link.Identified.Orphan> {
    return map { it.toOrphan() }
}
