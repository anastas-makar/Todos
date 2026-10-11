package pro.progr.todos.datefilters

import android.content.Context
import pro.progr.todos.R
import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DatesInMonthFilter : DatesFilter {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.every_month)
    }

    override fun getDescription(schedule: Schedule, context: Context): String {
        schedule.dates?.addedDates?.sort()
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        val dates = schedule.dates?.addedDates?.map { longVal ->
            LocalDate.ofEpochDay(longVal).format(formatter)
        }?.joinToString(", ").orEmpty()
        return context.getString(R.string.days_each_month, dates)
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return schedule.dates?.addedDates?.any { longVal ->
            val scheduleDate = LocalDate.ofEpochDay(longVal)
            scheduleDate.dayOfMonth == date.dayOfMonth
        } ?: false
    }
}
