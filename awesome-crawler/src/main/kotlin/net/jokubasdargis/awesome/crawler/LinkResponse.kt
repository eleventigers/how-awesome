package net.jokubasdargis.awesome.crawler

import java.io.InputStream

internal data class LinkResponse(val stream: InputStream, val contentLength: Long) {
}