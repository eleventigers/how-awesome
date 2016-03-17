package net.jokubasdargis.awesome.util

class Functions {
    companion object {
        private val OR: (Boolean, Boolean) -> Boolean = { a, b -> a || b }
        private val AND: (Boolean, Boolean) -> Boolean = { a, b -> a && b }

        fun <T> or(vararg f: (T) -> Boolean): (T) -> Boolean = { reduce(false, OR, *f) (it) }

        fun <T> and(vararg f: (T) -> Boolean): (T) -> Boolean = { reduce(true, AND, *f) (it) }

        fun <T> not(f: (T) -> Boolean): (T) -> Boolean = { t: T -> !f(t) }

        fun <T> always(condition: Boolean): (T) -> Boolean = { condition }

        fun <T, R> reduce(initial: R, compose: (R, R) -> R, vararg f: (T) -> R): (T) -> R {
            return { t: T ->
                f.fold(initial, { acc, f -> compose(acc, f(t)) })
            }
        }

        fun <R> memoize(f: () -> R): () -> R {
            var value : R? = null
            return {
                if (value == null) {
                    value = f()
                }
                value!!
            }
        }
    }
}
