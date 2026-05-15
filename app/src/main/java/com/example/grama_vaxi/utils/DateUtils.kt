package com.example.grama_vaxi.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {

    fun currentEpochDayUtc(): Long = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())

    fun epochDayToDisplay(epochDay: Long): String {
        if (epochDay <= 0) return "---"
        val millis = TimeUnit.DAYS.toMillis(epochDay)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
    }

    /**
     * Parses date and time into absolute epoch milliseconds.
     * Supports formats: "dd/MM/yyyy" and "yyyy-MM-dd".
     */
    fun parseDateTimeToEpoch(dateStr: String?, timeStr: String?): Long? {
        if (dateStr == null || timeStr == null) return null
        return try {
            val datePart = if (dateStr.contains("-")) {
                // yyyy-MM-dd -> dd/MM/yyyy
                val parts = dateStr.split("-")
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                dateStr
            }
            
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            format.parse("$datePart $timeStr")?.time
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parses a date string into an epoch day (days since 1970-01-01).
     * Supports formats: "dd/MM/yyyy" and "yyyy-MM-dd".
     */
    fun parseDateToEpochDay(dateStr: String?): Long? {
        if (dateStr == null) return null
        return try {
            val format = if (dateStr.contains("-")) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            } else {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            }
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date = format.parse(dateStr)
            date?.let { TimeUnit.MILLISECONDS.toDays(it.time) }
        } catch (e: Exception) {
            null
        }
    }
}
