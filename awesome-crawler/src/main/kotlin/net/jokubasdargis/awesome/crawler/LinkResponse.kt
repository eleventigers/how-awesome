package net.jokubasdargis.awesome.crawler

import java.io.InputStream

data class LinkResponse(val stream: InputStream, val contentLength: Long) {
}
