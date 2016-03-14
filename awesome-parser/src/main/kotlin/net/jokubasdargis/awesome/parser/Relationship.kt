package net.jokubasdargis.awesome.parser

interface Relationship<T> {
    fun from() : T
    fun to() : T
}
