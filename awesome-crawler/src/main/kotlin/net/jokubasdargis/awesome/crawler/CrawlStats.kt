package net.jokubasdargis.awesome.crawler

import java.time.Duration

data class CrawlStats(val duration: Duration, val status: CrawlStatus) {
    fun isSuccess(): Boolean {
        return status is CrawlStatus.Success
    }
}
