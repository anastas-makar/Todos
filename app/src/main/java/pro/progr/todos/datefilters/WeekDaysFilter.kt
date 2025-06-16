package pro.progr.todos.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class WeekDaysFilter : DatesFilter {
    override fun getTitle(): String {
        return "Дни недели"
    }

    override fun getDescription(schedule: Schedule): String {

        return "Дни недели: " + schedule.pattern.days.keys.sorted().map { dayOfWeekNumber ->
            DayOfWeek.of(dayOfWeekNumber.toInt()).getDisplayName(TextStyle.FULL, Locale.getDefault()).lowercase(
                Locale.getDefault()
            )
        }.joinToString()
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {

        if (schedule.dates?.till != null && !date.isBefore(LocalDate.ofEpochDay(schedule.dates!!.till!!))) {
            return false
        }

        if (schedule.dates?.since != null && date.isBefore(LocalDate.ofEpochDay(schedule.dates!!.since!!))) {
            return false
        }

        return schedule.pattern.days.containsKey(date.dayOfWeek.value.toLong())
    }
}