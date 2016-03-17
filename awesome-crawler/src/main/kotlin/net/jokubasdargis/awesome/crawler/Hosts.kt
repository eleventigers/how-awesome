package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Host

internal class Hosts private constructor(){
    companion object {
        private val GITHUB = Host.from("github.com")!!

        fun github(): Host {
            return GITHUB
        }
    }
}