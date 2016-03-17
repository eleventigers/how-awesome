package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.ContentType
import net.jokubasdargis.awesome.core.Host
import net.jokubasdargis.awesome.core.Link

internal class LinkFilters private constructor() {
    companion object {
        fun of(vararg host: Host): (Link) -> Boolean {
            return {
                when (it) {
                    is Link.Identified -> host.contains(it.host())
                    else -> false
                }
            }
        }

        fun of(vararg contentType: ContentType): (Link) -> Boolean {
            val detector = TikaContentTypeDetector.get()
            return {
                when (it) {
                    is Link.Identified -> contentType.contains(detector(null, it.canonicalize()))
                    else -> false
                }
            }
        }

        fun lru(vararg seed: Link): (Link) -> Boolean {
            val filter = LruLinkFilter.create()
            seed.forEach { filter(it) }
            return filter
        }

        fun combined(vararg filters: (Link) -> Boolean): (Link) -> Boolean {
            return { link ->
                filters.fold(true, { acc, filter ->
                    if (!acc) acc else filter(link)
                })
            }
        }
    }
}
