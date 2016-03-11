package net.jokubasdargis.awesome.processor

interface Relationship<T> {
    fun from() : T
    fun to() : T
}
