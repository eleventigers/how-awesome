package net.jokubasdargis.awesome.crawler

import net.jokubasdargis.awesome.core.Link
import net.jokubasdargis.awesome.parser.DocumentDefinition
import net.jokubasdargis.awesome.parser.LinkDefinition
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

internal class AwesomeContentPersistor private constructor() :
        (Iterable<DocumentDefinition>) -> Unit {

    private val writeLock = Any()

    private val links = Collections.newSetFromMap(ConcurrentHashMap<Link, Boolean>())
    private val linkTitles = Collections.newSetFromMap(
            ConcurrentHashMap<LinkDefinition.Title, Boolean>())
    private val linkDescriptions = Collections.newSetFromMap(
            ConcurrentHashMap<LinkDefinition.Description, Boolean>())

    override fun invoke(definitions: Iterable<DocumentDefinition>) {
        synchronized(writeLock) {
            definitions.forEach {
                when (it) {
                    is DocumentDefinition.Links -> {
                        links.addAll(it())

                    }
                    is DocumentDefinition.LinkDefinitions -> {
                        it().forEach {
                            when (it) {
                                is LinkDefinition.Title -> linkTitles.add(it)
                                is LinkDefinition.Description -> linkDescriptions.add(it)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun create(): (Iterable<DocumentDefinition>) -> Unit {
            return AwesomeContentPersistor()
        }
    }
}
