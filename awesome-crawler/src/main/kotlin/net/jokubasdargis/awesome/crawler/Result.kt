package net.jokubasdargis.awesome.crawler

sealed class Result<out T> {
    class Success<T>(val value: T) : Result<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Success<*>) {
                return false
            }

            if (value != other.value) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }

        override fun toString(): String {
            return "Success(value=$value)"
        }
    }

    class Failure<T>(val error: Throwable) : Result<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is Failure<*>) {
                return false
            }

            if (error != other.error) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            return error.hashCode()
        }

        override fun toString(): String {
            return "Failure(error=$error)"
        }
    }
}
