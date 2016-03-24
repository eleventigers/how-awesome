package net.jokubasdargis.awesome.core

sealed class DocumentDefinition(val documentLink: Link) {

    class Links(documentLink: Link, private val value: Set<Link>) :
            DocumentDefinition(documentLink), () -> Set<Link> {

        override fun invoke(): Set<Link> {
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
            if (!super.equals(other)) {
                return false
            }
            if (value != other.value) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result += 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String {
            return "Links(value=$value)"
        }
    }

    class LinkDefinitions(documentLink: Link, private val value: Set<LinkDefinition>) :
            DocumentDefinition(documentLink), () -> Set<LinkDefinition> {

        override fun invoke(): Set<LinkDefinition> {
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is LinkDefinitions) {
                return false
            }
            if (!super.equals(other)) {
                return false
            }
            if (value != other.value) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result += 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String {
            return "LinkDefinitions(value=$value)"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is DocumentDefinition) {
            return false
        }
        if (documentLink != other.documentLink) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        return documentLink.hashCode()
    }
}

fun <T : Iterable<DocumentDefinition>> T.links(): Iterable<DocumentDefinition.Links> {
    return filter { it is DocumentDefinition.Links }
            .map { it as DocumentDefinition.Links }
}

fun <T : Iterable<DocumentDefinition>> T.linkDefinitions()
        : Iterable<DocumentDefinition.LinkDefinitions> {
    return filter { it is DocumentDefinition.LinkDefinitions }
            .map { it as DocumentDefinition.LinkDefinitions }
}
