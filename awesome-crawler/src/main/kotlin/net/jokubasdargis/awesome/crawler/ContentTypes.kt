package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.ContentType


internal class ContentTypes private constructor() {
    companion object {
        private val HTML = ContentType.from("text/html")!!
        private val OCTET_STREAM = ContentType.from("application/octet-stream")!!

        fun html() : ContentType {
            return HTML
        }

        fun octetStream() : ContentType {
            return OCTET_STREAM
        }
    }
}
