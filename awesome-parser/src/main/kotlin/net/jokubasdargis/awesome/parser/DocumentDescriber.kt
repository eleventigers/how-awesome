package net.jokubasdargis.awesome.parser

import net.jokubasdargis.awesome.core.DocumentDescription
import net.jokubasdargis.awesome.core.Link
import java.io.Closeable

interface DocumentDescriber : Closeable, (Link) -> List<DocumentDescription> {
}
