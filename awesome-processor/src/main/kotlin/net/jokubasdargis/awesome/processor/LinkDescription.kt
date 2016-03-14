package net.jokubasdargis.awesome.processor

sealed class LinkDescription()  {

    class Title(val title: String): LinkDescription() {
        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Title) {
                return false
            }

            if (title != other.title) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            return title.hashCode()
        }
    }

    class Summary(val summary: String): LinkDescription() {
        override fun equals(other: Any?): Boolean{
            if (this === other) {
                return true
            }
            if (other !is Summary) {
                return false
            }

            if (summary != other.summary) {
                return false
            }

            return true
        }

        override fun hashCode(): Int{
            return summary.hashCode()
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
    }
}