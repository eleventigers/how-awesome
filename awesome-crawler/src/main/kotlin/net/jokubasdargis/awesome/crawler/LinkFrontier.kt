package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link

interface LinkFrontier : Iterator<Link>{
    fun add(link: Link): Boolean
    val size: Int
}