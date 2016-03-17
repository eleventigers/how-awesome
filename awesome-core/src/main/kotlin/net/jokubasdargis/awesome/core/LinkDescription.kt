package net.jokubasdargis.awesome.core

sealed class LinkDescription(val link: Link)  {

    class Title(link: Link, private val value: String): LinkDescription(link), () -> String {

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
            return "Title(value='$value')"
        }
    }

    class Summary(link: Link, private val value: String): LinkDescription(link), () -> String {

        override fun invoke(): String {
            return value
        }

        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Summary) {
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
            return "Summary(value='$value')"
        }
    }

    class None(link: Link): LinkDescription(link) {

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
            return "None()"
        }
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) {
            return true
        }
        if (other !is LinkDescription) {
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
}
