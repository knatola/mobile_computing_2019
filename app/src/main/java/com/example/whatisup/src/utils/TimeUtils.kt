package com.example.whatisup.src.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getToday(): String {
        val c = Calendar.getInstance().time
        val format = SimpleDateFormat("yyMMdd", Locale.getDefault())

        return format.format(c)
    }

    fun getTodayLong() : Long {
        return getToday().toLong()
    }

    fun getWeekAgo(howMany: Int): List<Long>{
        val df = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val retArr = mutableListOf<Long>()
        val multiplier = -7
        val farEnd = howMany * multiplier
        val lowerEnd = (howMany - 1) * multiplier

        var c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, farEnd)
        val olderDate = df.format(c.time).toLong()
        retArr.add(olderDate)

        c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, lowerEnd)
        val newerDate = df.format(c.time).toLong()
        retArr.add(newerDate)

        return retArr
    }

    fun getHourAndMinutes(time: Long): String {
        return String.format("%d h, %d m, %d s",
            TimeUnit.MILLISECONDS.toHours(time),
            TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(time)),
            TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        )
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    fun getDurationBreakdown(millis: Long): String {
        var millis = millis
        if (millis < 0) {
            throw IllegalArgumentException("Duration must be greater than zero!")
        }

        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

        val sb = StringBuilder(64)

        sb.append(hours)
        sb.append("h ")
        sb.append(minutes)
        sb.append("m ")
        sb.append(seconds)
        sb.append("s")

        return sb.toString()
    }

    /**
     * Formats yyMMdd Strings to MM-dd
     *
     * @param date the date as string
     * @return String the formatted string
     */
    fun formatDate(date: String): String {
        val chunks = date.chunked(2)
        return "${chunks[1]}-${chunks[2]}"
    }
}