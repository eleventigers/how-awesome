package net.jokubasdargis.awesome.crawler

import java.util.Locale

data class ContentType private constructor(val value: String) {

    fun apply(host: String?) : Boolean {
        return value.equals(host?.toLowerCase(Locale.ENGLISH));
    }

    companion object {
        fun from(value : String?) : ContentType? {
            if (value == null || value.isBlank()) {
                return null
            }
            return ContentType(value);
        }
    }
}