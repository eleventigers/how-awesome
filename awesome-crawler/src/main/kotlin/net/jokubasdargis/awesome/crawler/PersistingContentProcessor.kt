package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import java.io.InputStream

private class PersistingContentProcessor<T> private constructor(
        private val processor: ContentProcessor<T>,
        private val persistor: (T) -> Unit) : ContentProcessor<T> {

    override fun supportedContentTypes(): Set<ContentType> {
        return processor.supportedContentTypes()
    }

    override fun invoke(stream: InputStream, link: Link): T {
        val result = processor(stream, link)
        persistor(result)
        return result
    }

    companion object {
        fun <T> create(processor: ContentProcessor<T>,
                       persistor: (T) -> Unit): ContentProcessor<T> {
            return PersistingContentProcessor(processor, persistor)
        }
    }
}

internal fun <T> ContentProcessor<T>.withPersistor(persistor: (T) -> Unit): ContentProcessor<T> {
    return PersistingContentProcessor.create(this, persistor)
}
