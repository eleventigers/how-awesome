package net.jokubasdargis.awesome.message

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDefinition
import net.jokubasdargis.awesome.core.Result
import net.jokubasdargis.awesome.transport.LinkDefinitionDescription
import net.jokubasdargis.awesome.transport.LinkDefinitionForksCount
import net.jokubasdargis.awesome.transport.LinkDefinitionLatestCommitDate
import net.jokubasdargis.awesome.transport.LinkDefinitionRelationship
import net.jokubasdargis.awesome.transport.LinkDefinitionStarsCount
import net.jokubasdargis.awesome.transport.LinkDefinitionTitle
import java.io.IOException
import java.io.OutputStream
import java.util.Date

internal class ProtoMessageConverters private constructor() {
    companion object {

        //TODO(eleventigers, 23/03/16): handle null url Link conversions

        private class LinkConverter : MessageConverter<Link> {

            override fun from(bytes: ByteArray): Result<Link> {
                try {
                    val proto = net.jokubasdargis.awesome.transport.Link.parseFrom(bytes)
                    return Result.Success(Link.from(proto.url))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: Link, stream: OutputStream): Result<Int> {
                if (o is Link.Identified) {
                    try {
                        val proto = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(o.toUrl().toString())
                                .build()

                        proto.writeTo(stream)

                        return Result.Success(proto.serializedSize)
                    } catch (e: Exception) {
                        return Result.Failure(IOException(e))
                    }
                } else {
                    return Result.Failure(Exception("Cannot write link to stream: $o"))
                }
            }
        }

        private class LinkDefinitionRelationshipConverter :
                MessageConverter<LinkDefinition.Relationship> {

            override fun from(bytes: ByteArray): Result<LinkDefinition.Relationship> {
                try {
                    val proto = LinkDefinitionRelationship
                            .parseFrom(bytes)
                    return Result.Success(
                            LinkDefinition.Relationship(
                                    Link.from(proto.from.url), Link.from(proto.to.url)))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: LinkDefinition.Relationship,
                                  stream: OutputStream): Result<Int> {
                val from = o.link
                val to = o()
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

                        val protoDef = LinkDefinitionRelationship
                                .newBuilder()
                                .setFrom(protoLinkFrom)
                                .setTo(protoLinkTo)
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

            override fun from(bytes: ByteArray): Result<LinkDefinition.Title> {
                try {
                    val proto = LinkDefinitionTitle
                            .parseFrom(bytes)
                    return Result.Success(
                            LinkDefinition.Title(Link.from(proto.link.url), proto.value))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: LinkDefinition.Title, stream: OutputStream): Result<Int> {
                val link = o.link
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()

                        val protoDef = LinkDefinitionTitle
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(o())
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

            override fun from(bytes: ByteArray): Result<LinkDefinition.Description> {
                try {
                    val proto = LinkDefinitionDescription
                            .parseFrom(bytes)
                    return Result.Success(
                            LinkDefinition.Description(Link.from(proto.link.url), proto.value))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: LinkDefinition.Description, stream: OutputStream):
                    Result<Int> {
                val link = o.link
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()

                        val protoDef = LinkDefinitionDescription
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(o())
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

            override fun from(bytes: ByteArray): Result<LinkDefinition.StarsCount> {
                try {
                    val proto = LinkDefinitionStarsCount
                            .parseFrom(bytes)
                    return Result.Success(
                            LinkDefinition.StarsCount(Link.from(proto.link.url), proto.value))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: LinkDefinition.StarsCount, stream: OutputStream): Result<Int> {
                val link = o.link
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()

                        val protoDef = LinkDefinitionStarsCount
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(o())
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

            override fun from(bytes: ByteArray): Result<LinkDefinition.ForksCount> {
                try {
                    val proto = LinkDefinitionForksCount
                            .parseFrom(bytes)
                    return Result.Success(
                            LinkDefinition.ForksCount(Link.from(proto.link.url), proto.value))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: LinkDefinition.ForksCount, stream: OutputStream): Result<Int> {
                val link = o.link
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()

                        val protoDef = LinkDefinitionForksCount
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(o())
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

            override fun from(bytes: ByteArray): Result<LinkDefinition.LatestCommitDate> {
                try {
                    val proto = LinkDefinitionLatestCommitDate
                            .parseFrom(bytes)
                    val date = Date(proto.value)
                    return Result.Success(
                            LinkDefinition.LatestCommitDate(Link.from(proto.link.url), date))
                } catch (e: Exception) {
                    return Result.Failure(IOException(e))
                }
            }

            override fun toStream(o: LinkDefinition.LatestCommitDate, stream: OutputStream):
                    Result<Int> {
                val link = o.link
                if (link is Link.Identified) {
                    try {
                        val protoLink = net.jokubasdargis.awesome.transport.Link
                                .newBuilder()
                                .setUrl(link.toUrl().toString())
                                .build()

                        val protoDef = LinkDefinitionLatestCommitDate
                                .newBuilder()
                                .setLink(protoLink)
                                .setValue(o().time)
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

        fun link(): MessageConverter<Link> {
            return LinkConverter()
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