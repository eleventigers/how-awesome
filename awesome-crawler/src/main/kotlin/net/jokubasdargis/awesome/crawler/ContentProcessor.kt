package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import java.io.InputStream

interface ContentProcessor<R> : (InputStream, Link) -> R {
    fun supportedContentTypes(): Set<ContentType>
}
