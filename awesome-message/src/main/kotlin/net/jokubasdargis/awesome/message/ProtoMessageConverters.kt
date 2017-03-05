package net.jokubasdargis.awesome.message

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import net.jokubasdargis.awesome.core.LinkOccurrence
import net.jokubasdargis.awesome.core.Result
import net.jokubasdargis.awesome.transport.LinkDefinitionDescription
import net.jokubasdargis.awesome.transport.LinkDefinitionForksCount
import net.jokubasdargis.awesome.transport.LinkDefinitionLatestCommitDate
import net.jokubasdargis.awesome.transport.LinkDefinitionRelationship
import net.jokubasdargis.awesome.transport.LinkDefinitionStarsCount
import net.jokubasdargis.awesome.transport.LinkDefinitionTitle
import java.io.IOException
import java.io.OutputStream
import java.time.Instant

class ProtoMessageConverters private constructor() {
    companion object {

        //TODO(eleventigers, 23/03/16): handle null url Link conversions

        private class LinkOccurrenceConverter : MessageConverter<LinkOccurrence> {

            override fun from(bytes: ByteArray): Result<MessageParcel<LinkOccurrence>> {
                try {
                    val proto = net.jokubasdargis.awesome.transport.LinkOccurrence.parseFrom(bytes)
                    return Result.Success(MessageParcel(
                            LinkOccurrence(
                                    Link.from(proto.link.url),
                                    Link.from(proto.context.url)),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds,
                                    proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkOccurrence>,
                                  stream: OutputStream): Result<Int> {
                val link = o.value.link
                val context = o.value.context
                val timestamp = o.timestamp
                if (link is Link.Identified && context is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()
                        val protoContext = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(context.toUrl().toString())
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val proto = net.jokubasdargis.awesome.transport.LinkOccurrence
                                .newBuilder()
                                .setLink(protoLink)
                                .setContext(protoContext)
                                .setTimestamp(protoTimestamp)
                                .build()
                        proto.writeTo(stream)
                        return Result.Success(proto.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write occurrence to stream: $o"))
                }
            }
        }

        private class LinkDefinitionRelationshipConverter :
                MessageConverter<LinkDefinition.Relationship> {

            override fun from(bytes: ByteArray):
                    Result<MessageParcel<LinkDefinition.Relationship>> {
                try {
                    val proto = LinkDefinitionRelationship
                            .parseFrom(bytes)
                    return Result.Success(MessageParcel(
                            LinkDefinition.Relationship(
                                    Link.from(proto.from.url), Link.from(proto.to.url)),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds, proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkDefinition.Relationship>,
                                  stream: OutputStream): Result<Int> {
                val from = o.value.link
                val to = o.value()
                val timestamp = o.timestamp
                if (from is Link.Identified && to is Link.Identified) {
                    try {
                        val protoLinkFrom = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(from.toUrl().toString())
                                .build()
                        val protoLinkTo = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(to.toUrl().toString())
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val protoDef = LinkDefinitionRelationship
                                .newBuilder()
                                .setFrom(protoLinkFrom)
                                .setTo(protoLinkTo)
                                .setTimestamp(protoTimestamp)
                                .build()
                        protoDef.writeTo(stream)
                        return Result.Success(protoDef.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write definition to stream: $o"))
                }
            }
        }

        private class LinkDefinitionTitleConverter : MessageConverter<LinkDefinition.Title> {

            override fun from(bytes: ByteArray): Result<MessageParcel<LinkDefinition.Title>> {
                try {
                    val proto = LinkDefinitionTitle
                            .parseFrom(bytes)
                    return Result.Success(MessageParcel(
                            LinkDefinition.Title(
                                    Link.from(proto.link.url), proto.value),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds, proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkDefinition.Title>,
                                  stream: OutputStream): Result<Int> {
                val link = o.value.link
                val title = o.value()
                val timestamp = o.timestamp
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val protoDef = LinkDefinitionTitle
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(title)
                                .setTimestamp(protoTimestamp)
                                .build()
                        protoDef.writeTo(stream)
                        return Result.Success(protoDef.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write definition to stream: $o"))
                }
            }
        }

        private class LinkDefinitionDescriptionConverter :
                MessageConverter<LinkDefinition.Description> {

            override fun from(bytes: ByteArray):
                    Result<MessageParcel<LinkDefinition.Description>> {
                try {
                    val proto = LinkDefinitionDescription
                            .parseFrom(bytes)
                    return Result.Success(MessageParcel(
                            LinkDefinition.Description(Link.from(proto.link.url), proto.value),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds, proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkDefinition.Description>,
                                  stream: OutputStream): Result<Int> {
                val link = o.value.link
                val description = o.value()
                val timestamp = o.timestamp
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val protoDef = LinkDefinitionDescription
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(description)
                                .setTimestamp(protoTimestamp)
                                .build()
                        protoDef.writeTo(stream)
                        return Result.Success(protoDef.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write definition to stream: $o"))
                }
            }
        }

        private class LinkDefinitionStarsCountConverter :
                MessageConverter<LinkDefinition.StarsCount> {

            override fun from(bytes: ByteArray): Result<MessageParcel<LinkDefinition.StarsCount>> {
                try {
                    val proto = LinkDefinitionStarsCount
                            .parseFrom(bytes)
                    return Result.Success(MessageParcel(
                            LinkDefinition.StarsCount(Link.from(proto.link.url), proto.value),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds, proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkDefinition.StarsCount>,
                                  stream: OutputStream): Result<Int> {
                val link = o.value.link
                val stars = o.value()
                val timestamp = o.timestamp
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val protoDef = LinkDefinitionStarsCount
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(stars)
                                .setTimestamp(protoTimestamp)
                                .build()
                        protoDef.writeTo(stream)
                        return Result.Success(protoDef.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write definition to stream: $o"))
                }
            }
        }

        private class LinkDefinitionForksCountConverter :
                MessageConverter<LinkDefinition.ForksCount> {

            override fun from(bytes: ByteArray): Result<MessageParcel<LinkDefinition.ForksCount>> {
                try {
                    val proto = LinkDefinitionForksCount
                            .parseFrom(bytes)
                    return Result.Success(MessageParcel(
                            LinkDefinition.ForksCount(Link.from(proto.link.url), proto.value),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds, proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkDefinition.ForksCount>,
                                  stream: OutputStream): Result<Int> {
                val link = o.value.link
                val forks = o.value()
                val timestamp = o.timestamp
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val protoDef = LinkDefinitionForksCount
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(forks)
                                .setTimestamp(protoTimestamp)
                                .build()
                        protoDef.writeTo(stream)
                        return Result.Success(protoDef.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write definition to stream: $o"))
                }
            }
        }

        private class LinkDefinitionLatestCommitDateConverter :
                MessageConverter<LinkDefinition.LatestCommitDate> {

            override fun from(bytes: ByteArray):
                    Result<MessageParcel<LinkDefinition.LatestCommitDate>> {
                try {
                    val proto = LinkDefinitionLatestCommitDate
                            .parseFrom(bytes)
                    val date = Instant.ofEpochSecond(
                            proto.value.seconds, proto.value.nanos.toLong())
                    return Result.Success(MessageParcel(
                            LinkDefinition.LatestCommitDate(Link.from(proto.link.url), date),
                            Instant.ofEpochSecond(
                                    proto.timestamp.seconds, proto.timestamp.nanos.toLong())))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: MessageParcel<LinkDefinition.LatestCommitDate>,
                                  stream: OutputStream):
                    Result<Int> {
                val link = o.value.link
                val date = o.value()
                val timestamp = o.timestamp
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()
                        val protoDate = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(date.epochSecond)
                                .setNanos(date.nano)
                                .build()
                        val protoTimestamp = com.google.protobuf.Timestamp
                                .newBuilder()
                                .setSeconds(timestamp.epochSecond)
                                .setNanos(timestamp.nano)
                                .build()
                        val protoDef = LinkDefinitionLatestCommitDate
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(protoDate)
                                .setTimestamp(protoTimestamp)
                                .build()
                        protoDef.writeTo(stream)
                        return Result.Success(protoDef.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write definition to stream: $o"))
                }
            }
        }

        fun linkOccurrence(): MessageConverter<LinkOccurrence> {
            return LinkOccurrenceConverter()
        }

        fun linkDefinitionRelationship(): MessageConverter<LinkDefinition.Relationship> {
            return LinkDefinitionRelationshipConverter()
        }

        fun linkDefinitionTitle(): MessageConverter<LinkDefinition.Title> {
            return LinkDefinitionTitleConverter()
        }

        fun linkDefinitionDescription(): MessageConverter<LinkDefinition.Description> {
            return LinkDefinitionDescriptionConverter()
        }

        fun linkDefinitionStarsCount(): MessageConverter<LinkDefinition.StarsCount> {
            return LinkDefinitionStarsCountConverter()
        }

        fun linkDefinitionForksCount(): MessageConverter<LinkDefinition.ForksCount> {
            return LinkDefinitionForksCountConverter()
        }

        fun linkDefinitionLatestCommitDate(): MessageConverter<LinkDefinition.LatestCommitDate> {
            return LinkDefinitionLatestCommitDateConverter()
        }
    }
}