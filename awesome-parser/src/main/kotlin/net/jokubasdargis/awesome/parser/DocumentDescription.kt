package net.jokubasdargis.awesome.parser


sealed class DocumentDescription {

    class Links(val links: List<Link>) : DocumentDescription() {

        @Suppress("UNCHECKED_CAST")
        fun invalid(): List<Link.Invalid> {
            return links.filter { it is Link.Invalid } as List<Link.Invalid>
        }

        @Suppress("UNCHECKED_CAST")
        fun identified(): List<Link.Identified> {
            return links.filter { it is Link.Identified } as List<Link.Identified>
        }

        fun ofHost(host: Host): List<Link> {
            return identified().filter { it.ofHost(host) }
        }

        fun notOfHost(host: Host): Set<Link> {
            return links.subtract(ofHost(host))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Links) {
                return false
            }

            if (links != other.links) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return links.hashCode()
        }
    }

    class LinkDescriptions(
            val descriptions: Map<Link, List<LinkDescription>>) : DocumentDescription() {
        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is LinkDescriptions) {
                return false
            }

            if (descriptions != other.descriptions) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            return descriptions.hashCode()
        }
    }

    class LinkRelationships(
            val relationships: List<Relationship<Link>>) : DocumentDescription() {
        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is LinkRelationships) {
                return false
            }

            if (relationships != other.relationships) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            return relationships.hashCode()
        }
    }
}
