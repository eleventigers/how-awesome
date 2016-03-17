package net.jokubasdargis.awesome.crawler

import com.google.common.cache.Cache
import com.google.common.truth.Truth.assertThat
import net.jokubasdargis.awesome.core.Link
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
class LruLinkFilterTest {

    companion object {
        private val KEY_GENERATOR_CANONICAL: (Link.Identified) -> String = { it.canonicalize() }
        private val LINK_INVALID = Link.from("//")
        private val LINK_A = Link.from("http://google.com") as Link.Identified
    }

    private val cache: Cache<String, Any> = mock(Cache::class.java) as Cache<String, Any>
    private val sut = LruLinkFilter.create(cache, KEY_GENERATOR_CANONICAL)

    @Test fun nonIdentifiedLink() {
        val filter = sut(LINK_INVALID)

        assertThat(filter).isFalse()
    }

    @Test fun unseenLink() {
        `when`(cache.getIfPresent(KEY_GENERATOR_CANONICAL(LINK_A))).thenReturn(null)

        val filter = sut(LINK_A)

        assertThat(filter).isTrue()
    }

    @Test fun seenLink() {
        `when`(cache.getIfPresent(KEY_GENERATOR_CANONICAL(LINK_A))).thenReturn(Any())

        val filter = sut(LINK_A)

        assertThat(filter).isFalse()
    }

    @Test fun unseenThenSeenLink() {
        `when`(cache.getIfPresent(KEY_GENERATOR_CANONICAL(LINK_A))).thenReturn(null)

        val firstFilter = sut(LINK_A)

        `when`(cache.getIfPresent(KEY_GENERATOR_CANONICAL(LINK_A))).thenReturn(Any())

        val secondFilter = sut(LINK_A)

        assertThat(firstFilter).isTrue()
        assertThat(secondFilter).isFalse()
    }

}