package net.jokubasdargis.awesome.core

import java.util.Locale

data class Host private constructor(val name: String) {

    fun apply(host: String?) : Boolean {
        return name.equals(host?.toLowerCase(Locale.ENGLISH));
    }

    companion object {
        val GITHUB = from("github.com")!!

        fun from(value : String?) : Host? {
            if (value == null || value.isBlank()) {
                return null
            }
            return Host(value);
        }
    }
}
