package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.ContentType
import org.apache.tika.detect.DefaultDetector
import org.apache.tika.detect.Detector
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaMetadataKeys
import java.io.InputStream

internal class TikaContentTypeDetector private constructor(
        val detector: Detector) : (InputStream?, String?) -> ContentType? {

    override fun invoke(stream: InputStream?, resName: String?): ContentType? {
        val metaData = if (!resName.isNullOrBlank()) {
            val m = Metadata()
            m.add(TikaMetadataKeys.RESOURCE_NAME_KEY, resName)
            m
        } else METADATA_EMPTY
        val mediaType = detector.detect(stream, metaData)
        return ContentType.from(mediaType?.toString())
    }

    companion object {
        private val METADATA_EMPTY = Metadata()
        private val DETECTOR = DefaultDetector()

        private class Holder {
            companion object {
                val INSTANCE = create()
            }
        }

        fun get(): (InputStream?, String?) -> ContentType? {
            return Holder.INSTANCE
        }

        fun create(): (InputStream?, String?) -> ContentType? {
            return TikaContentTypeDetector(DETECTOR)
        }
    }
}
