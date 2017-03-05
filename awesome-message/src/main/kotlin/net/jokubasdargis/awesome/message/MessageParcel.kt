package net.jokubasdargis.awesome.message

import java.time.Instant

data class MessageParcel<out T>(val value: T, val timestamp: Instant)
