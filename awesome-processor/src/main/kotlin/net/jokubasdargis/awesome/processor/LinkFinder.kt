package net.jokubasdargis.awesome.processor

interface LinkFinder {
    fun find(rootLink: Link) : LinkList
}
