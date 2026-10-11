package pro.progr.todos.datefilters

import android.content.Context
import pro.progr.todos.R
import pro.progr.diamondsandberries.db.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class WeekDaysFilter : DatesFilter {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.weekdays)
    }

    override fun getDescription(schedule: Schedule, context: Context): String {

        val days = schedule.pattern.days.keys.sorted().map { dayOfWeekNumber ->
            DayOfWeek.of(dayOfWeekNumber.toInt()).getDisplayName(TextStyle.FULL, Locale.getDefault()).lowercase(
                Locale.getDefault()
            )
        }.joinToString()
        return context.getString(R.string.days_of_week_list, days)
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
