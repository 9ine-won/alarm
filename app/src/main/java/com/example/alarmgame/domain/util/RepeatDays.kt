package com.example.alarmgame.domain.util

import java.time.DayOfWeek

object RepeatDays {
    /** Bitmask helper: Sunday=1 shl 0 ... Saturday=1 shl 6. */
    private val dayToBit = DayOfWeek.values().associateWith { day ->
        1 shl ((day.ordinal + 1) % 7)
    }

    fun maskOf(days: Set<DayOfWeek>): Int = days.fold(0) { acc, day -> acc or (dayToBit[day] ?: 0) }

    fun daysFrom(mask: Int): Set<DayOfWeek> =
        dayToBit.entries.filter { (day, bit) -> mask and bit != 0 }.mapTo(sortedSetOf(), Map.Entry<DayOfWeek, Int>::key)
}
