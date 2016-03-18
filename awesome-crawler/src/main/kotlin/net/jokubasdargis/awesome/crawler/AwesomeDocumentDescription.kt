package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.core.LinkDescription
import net.jokubasdargis.awesome.core.Relationship

sealed class AwesomeDocumentDescription(val documentLink: Link) {

    class Links(documentLink: Link, val value: Set<Link.Identified>) :
            AwesomeDocumentDescription(documentLink), () -> Set<Link.Identified> {

        override fun invoke(): Set<Link.Identified> {
            return value
        }

        override fun equals(other: Any?): Boolean{
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

        override fun hashCode(): Int{
            var result = super.hashCode()
            result += 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String{
            return "Links(documentLink=$documentLink, value=$value)"
        }
    }

    class LinkDescriptions(documentLink: Link, val value: Set<LinkDescription>):
            AwesomeDocumentDescription(documentLink), () -> Set<LinkDescription> {

        override fun invoke(): Set<LinkDescription> {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is LinkDescriptions) {
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

        override fun hashCode(): Int{
            var result = super.hashCode()
            result += 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String{
            return "LinkDescriptions(documentLink=$documentLink, value=$value)"
        }
    }

    class LinkRelationships<T: Link.Identified>(documentLink: Link, val value: Set<Relationship<T>>):
            AwesomeDocumentDescription(documentLink), () -> Set<Relationship<T>> {

        override fun invoke(): Set<Relationship<T>> {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is LinkRelationships<*>) {
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

        override fun hashCode(): Int{
            var result = super.hashCode()
            result += 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String{
            return "Relationships(documentLink=$documentLink, value=$value)"
        }
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) {
            return true
        }
        if (other !is AwesomeDocumentDescription) {
            return false
        }
        if (documentLink != other.documentLink) {
            return false
        }
        return true
    }

    override fun hashCode(): Int{
        return documentLink.hashCode()
    }
}