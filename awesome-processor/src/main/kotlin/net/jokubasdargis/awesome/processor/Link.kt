package net.jokubasdargis.awesome.processor

import io.mola.galimatias.URL
import java.net.URI

data class Link private constructor(
        private val url: URL, private val parent: Link? = null, private val raw: String) {

    fun toUri(): URI {
        return url.toJavaURI()
    }

    fun toUrl(): java.net.URL {
        return url.toJavaURL()
    }

    fun raw(): String {
        return raw
    }

    fun ofHost(host: Host): Boolean {
        return host.apply(url.host().toString())
    }

    fun equalHierarchy(link: Link) : Boolean {
        if (!url.isHierarchical || !link.url.isHierarchical) {
            return false
        }
        if (!url.authority().equals(link.url.authority())) {
            return false
        }
        if (!url.path().equals(link.url.path())) {
            return false
        }
        return true
    }

    override fun toString(): String{
        return "Link(url=$url, parent=$parent)"
    }

    companion object {
        fun from(string: String, parent: Link? = null): Link {
            return Link(URL.fromJavaURI(resolve(URI(string), parent)), parent, string)
        }

        private fun resolve(uri: URI, parent: Link?): URI {
            return if (uri.host != null) uri else {
                parent?.toUri()?.resolve(uri.toString()) ?: uri
            }
        }
    }
}
