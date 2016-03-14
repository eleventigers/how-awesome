package net.jokubasdargis.awesome.processor

class LinkList private constructor(pairs: List<Pair<Link, List<Link>>>) {

    private val pairs = pairs;

    fun links() : List<Link> {
        return pairs.map { p -> p.first }
    }

    fun relationships() : List<Relationship<Link>> {
        return pairs.map { p: Pair<Link, List<Link>> ->
            p.second.map {
                LinkRelationship(p.first, it)
            }
        }.reduce { a, l -> a.plus(l) }
    }

    internal companion object{
        fun from(pairs: List<Pair<Link, List<Link>>>) : LinkList {
            return LinkList(pairs)
        }
    }

    internal class LinkRelationship(val from : Link, val to : Link) : Relationship<Link> {
        override fun from(): Link {
            return from
        }

        override fun to(): Link {
            return to
        }

        override fun toString(): String {
            return "${from.raw()} -> ${to.raw()}"
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) return true
            if (other !is LinkRelationship) return false

            if (from != other.from) return false
            if (to != other.to) return false

            return true
        }

        override fun hashCode(): Int{
            var result = from.hashCode()
            result += 31 * result + to.hashCode()
            return result
        }
    }
}