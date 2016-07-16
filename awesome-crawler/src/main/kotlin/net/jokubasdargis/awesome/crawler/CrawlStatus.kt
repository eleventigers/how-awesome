package net.jokubasdargis.awesome.crawler

sealed class CrawlStatus {
    class Success(): CrawlStatus() {
        override fun toString(): String{
            return "Success()"
        }
    }

    class Failure(val error: Throwable): CrawlStatus() {
        override fun toString(): String{
            return "Failure(error=$error)"
        }
    }
}
