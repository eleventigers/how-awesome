package net.jokubasdargis.awesome.crawler

internal class CrawlException: Exception {
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
    constructor(message: String, cause: Throwable): super(message, cause)
}
