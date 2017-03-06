package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link

internal class CrawlException: Exception {
    val link: Link?
    constructor(link: Link?, message: String) : super(message) {
        this.link = link
    }
    constructor(link: Link?, cause: Throwable) : super(cause) {
        this.link = link
    }
}
