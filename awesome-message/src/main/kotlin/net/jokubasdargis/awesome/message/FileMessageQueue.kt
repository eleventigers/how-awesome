package net.jokubasdargis.awesome.message

import com.squareup.tape.FileObjectQueue
import net.jokubasdargis.awesome.core.Result
import java.io.File
import java.io.OutputStream

class FileMessageQueue<T> private constructor(
        private val queue: FileObjectQueue<MessageParcel<T>>) : MessageQueue<T> {
    override fun add(value: MessageParcel<T>): Boolean {
        try {
            queue.add(value)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun peek(): MessageParcel<T>? {
        return queue.peek()
    }

    override fun close() {
       queue.close()
    }

    override fun remove() {
       queue.remove()
    }

    override fun isEmpty(): Boolean {
        return size <= 0
    }

    override val size: Long
        get() = queue.size().toLong()

    companion object {
        private class FileObjectQueueMessageConverter<T>(
                private val converter: MessageConverter<T>) :
                FileObjectQueue.Converter<MessageParcel<T>> {

            override fun toStream(value: MessageParcel<T>?, stream: OutputStream?) {
                if (value != null && stream != null) {
                    converter.toStream(value, stream)
                }
            }

            override fun from(bytes: ByteArray?): MessageParcel<T>? {
                if (bytes != null) {
                    val result = converter.from(bytes)
                    if (result is Result.Success) {
                        return result.value
                    }
                }
                return null
            }
        }

        fun <T> create(file: File, converter: MessageConverter<T>) : MessageQueue<T> {
            return FileMessageQueue(
                    FileObjectQueue(file, FileObjectQueueMessageConverter(converter)))
        }
    }
}
