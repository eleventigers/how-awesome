package net.jokubasdargis.awesome.core

import java.time.Instant
import java.util.Date

sealed class LinkDefinition(val link: Link)  {

    class Title(link: Link, private val value: String): LinkDefinition(link), () -> String {

        override fun invoke(): String {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Title) {
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
            return "Title(value='$value', ${super.toString()})"
        }
    }

    class Description(link: Link, private val value: String): LinkDefinition(link), () -> String {

        override fun invoke(): String {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Description) {
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
            return "Description(value='$value', ${super.toString()})"
        }
    }

    class Relationship(link: Link, private val value: Link):
            LinkDefinition(link), net.jokubasdargis.awesome.core.Relationship<Link>, () -> Link {

        override fun invoke(): Link {
            return value
        }

        override fun from(): Link {
            return link
        }

        override fun to(): Link {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Relationship) {
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

        override fun toString(): String {
            return "Relationship(${link.raw} -> ${value.raw}, ${super.toString()})"
        }
    }

    class StarsCount(link: Link, private val value: Int): LinkDefinition(link), () -> Int {

        override fun invoke(): Int {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is StarsCount) {
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
            result += 31 * result + value
            return result
        }

        override fun toString(): String{
            return "StarsCount(value=$value, ${super.toString()})"
        }
    }

    class ForksCount(link: Link, private val value: Int): LinkDefinition(link), () -> Int {

        override fun invoke(): Int {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is ForksCount) {
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
            result += 31 * result + value
            return result
        }

        override fun toString(): String{
            return "ForksCount(value=$value, ${super.toString()})"
        }
    }

    class LatestCommitDate(link: Link, private val value: Instant):
            LinkDefinition(link), () -> Instant {

        override fun invoke(): Instant {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other){
                return true
            }
            if (other !is LatestCommitDate) {
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
            return "LatestCommitDate(value=$value, ${super.toString()})"
        }
    }

    class None(link: Link): LinkDefinition(link) {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is None) {
                return false
            }
            if (!super.equals(other)) {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }

        override fun toString(): String{
            return "None(${super.toString()})"
        }
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) {
            return true
        }
        if (other !is LinkDefinition) {
            return false
        }
        if (link != other.link) {
            return false
        }
        return true
    }

    override fun hashCode(): Int{
        return link.hashCode()
    }

    override fun toString(): String {
        return "LinkDefinition(link=$link)"
    }
}
