package net.jokubasdargis.awesome.crawler

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import net.jokubasdargis.awesome.core.Link

internal class LruLinkFilter private constructor(
        private val cache: Cache<String, Any>,
        private val keyGenerator: (Link.Identified) -> String) : (Link) -> Boolean {

    override fun invoke(link: Link): Boolean {
        if (link !is Link.Identified) {
            return false
        }
        val key = keyGenerator(link)
        var cached = cache.getIfPresent(key)
        if (cached == null) {
            synchronized(cache, {
                cached = cache.getIfPresent(key)
                if (cached == null) {
                    cache.put(key, CACHE_VAL_INSTANCE)
                    return true
                } else {
                    return false
                }
            })
        } else {
            return false
        }
    }

    companion object {
        private val CACHE_VAL_INSTANCE = Any()
        private val KEY_GENERATOR_CANONICAL: (Link.Identified) -> String = { it.canonicalize() }

        fun create(cacheCapacity: Long = 49152,
                   keyGenerator: (Link.Identified) -> String = KEY_GENERATOR_CANONICAL):
                (Link) -> Boolean {
            return create(
                    CacheBuilder.newBuilder().maximumSize(cacheCapacity).build(), keyGenerator)
        }

        internal fun create(cache: Cache<String, Any>,
                            keyGenerator: (Link.Identified) -> String): (Link) -> Boolean {
            return LruLinkFilter(cache, keyGenerator)
        }
    }
}