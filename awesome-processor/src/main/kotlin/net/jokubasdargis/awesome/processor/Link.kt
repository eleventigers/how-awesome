package net.jokubasdargis.awesome.processor

import io.mola.galimatias.URL
import java.net.URI

sealed class Link(private val raw: String) {

    fun raw(): String {
        return raw
    }

    class Identified internal constructor(
            private val url: URL, val parent: Link? = null, raw: String) : Link(raw) {

        fun toUri(): URI {
            return url.toJavaURI()
        }

        fun toUrl(): java.net.URL {
            return url.toJavaURL()
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

        override fun toString(): String {
            return "Link(url=$url, parent=$parent)"
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Identified) {
                return false
            }

            if (url != other.url) {
                return false
            }
            if (parent != other.parent) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            var result = url.hashCode()
            result += 31 * result + (parent?.hashCode() ?: 0)
            return result
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
        fun from(string: String, parent: Link? = null): Link {
            try {
                val url = URL.fromJavaURI(resolve(URI(string), parent))
                return Link.Identified(url, parent, string)
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
