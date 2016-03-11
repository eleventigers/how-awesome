package net.jokubasdargis.awesome.processor

internal class Functions {
    companion object {
        private val OR: (Boolean, Boolean) -> Boolean = { a, b -> a || b }
        private val AND: (Boolean, Boolean) -> Boolean = { a, b -> a && b }

        fun <T> or(vararg f: (T) -> Boolean): (T) -> Boolean = { reduce(false, OR, *f) (it) }

        fun <T> and(vararg f: (T) -> Boolean): (T) -> Boolean = { reduce(true, AND, *f) (it) }

        fun <T> always(condition: Boolean): (T) -> Boolean = { condition }

        fun <T, R> reduce(initial: R, compose: (R, R) -> R, vararg f: (T) -> R): (T) -> R {
            return { t: T ->
                f.fold(initial, { r, f -> compose(r, f(t)) })
            }
        }
    }
}
