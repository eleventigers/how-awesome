package net.jokubasdargis.awesome.parser

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal class DateFormatter private constructor(private val formats: List<DateFormat>) {

    fun parse(string: String): Date? {
        formats.forEach {
            try {
                synchronized(it) {
                    return it.parse(string)
                }
            } catch (e: ParseException) {
                // ignored
            }
        }
        return null
    }

    fun format(date: Date): String {
        val primary = formats.first()
        synchronized(primary) {
            return primary.format(date)
        }
    }

    companion object {

        private val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        private class Holder {
            companion object {
                val INSTANCE_ISO_8601 = createIso8601()
            }
        }

        fun iso8601(): DateFormatter {
            return Holder.INSTANCE_ISO_8601
        }

        fun default(): DateFormatter {
            return iso8601()
        }

        fun createIso8601(): DateFormatter {
            val timeZone = TimeZone.getTimeZone("Zulu")
            val iso8601 = SimpleDateFormat(ISO_8601, Locale.ENGLISH)
            iso8601.timeZone = timeZone
            return DateFormatter(listOf(iso8601))
        }
    }
}