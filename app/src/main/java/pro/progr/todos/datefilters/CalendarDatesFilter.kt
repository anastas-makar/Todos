package pro.progr.todos.datefilters

import android.content.Context
import pro.progr.todos.R
import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CalendarDatesFilter : DatesFilter {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.exact_dates)
    }

    override fun getDescription(schedule: Schedule, context: Context) : String {
        schedule.dates?.addedDates?.sort()
        val dates = schedule.dates?.addedDates?.map {longVal ->
            LocalDate.ofEpochDay(longVal).format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }?.joinToString(", ").orEmpty()
        return context.getString(R.string.dates_list, dates)
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return schedule.dates?.addedDates?.contains(date.toEpochDay()) ?: false
    }
}
