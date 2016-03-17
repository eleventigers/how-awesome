package net.jokubasdargis.awesome.crawler

import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import com.google.common.io.ByteStreams
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.Collections
import java.util.HashSet

internal class FingerPrintContentFilter private constructor(
        private val hashes: MutableSet<Long>,
        private val hasher: (InputStream) -> Pair<Int, Long>) : (InputStream) -> Boolean {

    override fun invoke(stream: InputStream): Boolean {
        val start = System.currentTimeMillis()
        val result = hasher(stream)
        val duration = System.currentTimeMillis() - start

        LOGGER.debug("Content of ${result.first} bytes fingerprinted in ${duration}ms")

        synchronized(hashes) {
            if (hashes.contains(result.second)) {
                return false
            } else {
                hashes.add(result.second)
                return true
            }
        }
    }

    companion object {

        private class HashFunctionDelegateHasher(
                private val delegate: HashFunction) : (InputStream) -> Pair<Int, Long> {
            override fun invoke(stream: InputStream): Pair<Int, Long> {
                val bytes = ByteStreams.toByteArray(stream)
                val hash = delegate.hashBytes(bytes).asLong()
                return Pair(bytes.size, hash)
            }
        }

        private val LOGGER = LoggerFactory.getLogger(FingerPrintContentFilter::class.java)

        private class Holder {
            companion object {
                val INSTANCE = create()
            }
        }

        fun get(): (InputStream) -> Boolean {
            return Holder.INSTANCE
        }

        fun create(): (InputStream) -> Boolean {
            return FingerPrintContentFilter(
                    Collections.synchronizedSet(HashSet()),
                    HashFunctionDelegateHasher(Hashing.murmur3_128()))
        }
    }
}