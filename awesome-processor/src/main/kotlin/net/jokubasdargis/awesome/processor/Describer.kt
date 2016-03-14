package net.jokubasdargis.awesome.processor

interface Describer<T, R> {

    fun describe(value: T): R
}
