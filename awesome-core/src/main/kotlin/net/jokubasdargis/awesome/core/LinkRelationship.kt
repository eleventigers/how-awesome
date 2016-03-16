package net.jokubasdargis.awesome.core

class LinkRelationship(private val from: Link, private val to: Link) : Relationship<Link> {

    override fun from(): Link {
        return from
    }

    override fun to(): Link {
        return to
    }

    override fun toString(): String {
        return "${from.raw} -> ${to.raw}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is LinkRelationship) {
            return false
        }

        if (from != other.from) {
            return false
        }
        if (to != other.to) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result += 31 * result + to.hashCode()
        return result
    }
}

private class OrphanLinkRelationship(
        private val from: Link.Identified.Orphan,
        private val to: Link.Identified.Orphan) : Relationship<Link.Identified.Orphan> {

    override fun from(): Link.Identified.Orphan {
        return from
    }

    override fun to(): Link.Identified.Orphan {
        return to
    }

    override fun toString(): String {
        return "${from.toUrl()} -> ${to.toUrl()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is OrphanLinkRelationship) {
            return false
        }

        if (from != other.from) {
            return false
        }
        if (to != other.to) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result += 31 * result + to.hashCode()
        return result
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Iterable<Relationship<Link>>> T.identified(): Iterable<Relationship<Link.Identified>> {
    return filter { it.from() is Link.Identified && it.to() is Link.Identified }
            .map { it as Relationship<Link.Identified> }
}

fun <T : Iterable<Relationship<Link.Identified>>> T.asOrphans()
        : Iterable<Relationship<Link.Identified.Orphan>> {
    return map { OrphanLinkRelationship(it.from().toOrphan(), it.to().toOrphan()) }
}