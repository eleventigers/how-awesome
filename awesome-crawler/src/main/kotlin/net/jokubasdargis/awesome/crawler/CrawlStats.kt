package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.Result
import java.time.Duration

data class CrawlStats(val duration: Duration, val result: Result<Link>) {
    fun isSuccess(): Boolean {
        return result is Result.Success
    }
}
