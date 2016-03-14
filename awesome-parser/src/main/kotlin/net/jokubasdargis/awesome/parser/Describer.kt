package net.jokubasdargis.awesome.parser

interface Describer<T, R> {

    fun describe(value: T): R
}
