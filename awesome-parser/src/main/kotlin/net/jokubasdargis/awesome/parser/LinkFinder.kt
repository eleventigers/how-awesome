package net.jokubasdargis.awesome.parser

interface LinkFinder {
    fun find(rootLink: Link) : LinkList
}
