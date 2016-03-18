package net.jokubasdargis.awesome.core

sealed class DocumentDescription {

    class Links(private val value: List<Link>) : DocumentDescription(), () -> List<Link> {

        override fun invoke(): List<Link> {
            return value
        }

        @Suppress("UNCHECKED_CAST")
        fun invalid(): List<Link.Invalid> {
            return value.filter { it is Link.Invalid } as List<Link.Invalid>
        }

        @Suppress("UNCHECKED_CAST")
        fun identified(): List<Link.Identified> {
            return value.filter { it is Link.Identified } as List<Link.Identified>
        }

        fun ofHost(host: Host): List<Link> {
            return identified().filter { it.ofHost(host) }
        }

        fun notOfHost(host: Host): Set<Link> {
            return value.subtract(ofHost(host))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Links) {
                return false
            }
            if (value != other.value) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return "Links(value=$value)"
        }
    }

    class LinkDescriptions(private val value: List<LinkDescription>) :
            DocumentDescription(), () -> List<LinkDescription> {

        override fun invoke(): List<LinkDescription> {
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is LinkDescriptions) {
                return false
            }
            if (value != other.value) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return "LinkDescriptions(value=$value)"
        }
    }

    class LinkRelationships(private val value: List<Relationship<Link>>) :
            DocumentDescription(), () -> List<Relationship<Link>> {

        override fun invoke(): List<Relationship<Link>> {
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is LinkRelationships) {
                return false
            }
            if (value != other.value) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return "LinkRelationships(value=$value)"
        }
    }
}

fun <T : Iterable<DocumentDescription>> T.links(): Iterable<DocumentDescription.Links> {
    return filter { it is DocumentDescription.Links }
            .map { it as DocumentDescription.Links }
}

fun <T : Iterable<DocumentDescription>> T.linkDescriptions()
        : Iterable<DocumentDescription.LinkDescriptions> {
    return filter { it is DocumentDescription.LinkDescriptions }
            .map { it as DocumentDescription.LinkDescriptions }
}

fun <T : Iterable<DocumentDescription>> T.linkRelationships()
        : Iterable<DocumentDescription.LinkRelationships> {
    return filter { it is DocumentDescription.LinkRelationships }
            .map { it as DocumentDescription.LinkRelationships }
}
