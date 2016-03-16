package net.jokubasdargis.awesome.core

sealed class LinkDescription()  {

    class Title(private val value: String): LinkDescription(), () -> String {

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

            if (value != other.value) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            return value.hashCode()
        }

        override fun toString(): String{
            return "Title(value='$value')"
        }
    }

    class Summary(private val value: String): LinkDescription(), () -> String {

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

            if (value != other.value) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            return value.hashCode()
        }

        override fun toString(): String{
            return "Summary(value='$value')"
        }
    }

    class None(): LinkDescription() {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is None) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return 0
        }

        override fun toString(): String{
            return "None()"
        }
    }
}
