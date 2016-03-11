package net.jokubasdargis.awesome.processor

import java.net.URI
import java.net.URISyntaxException

data class Link(val uri: URI?, val parent: Link? = null) {

    fun isOf(host: Host): Boolean {
        return if (uri == null || uri.host == null) {
            host.apply(parent?.uri?.host)
        } else {
            host.apply(uri.host)
        }
    }

    companion object {
        fun from(link : String, parent: Link? = null) : Link {
            return Link(liftParent(uriOrNull(link), parent), parent)
        }

        private fun uriOrNull(string: String): URI? {
            return try {
                URI(string).normalize()
            } catch (e: URISyntaxException) {
                null
            }
        }

        private fun liftParent(uri: URI?, parent: Link?) : URI? {
            return if (uri?.host != null) uri else { parent?.uri?.resolve(uri)?: uri }
        }
    }
}
