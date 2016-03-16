package net.jokubasdargis.awesome.core

interface Relationship<T> {
    fun from() : T
    fun to() : T
}
