package pro.progr.todos.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CalendarDatesFilter : DatesFilter {
    override fun getTitle(): String {
        return "Точные даты"
    }

    override fun getDescription(schedule: Schedule) : String {
        schedule.dates?.addedDates?.sort()
        return "Даты: " + schedule.dates?.addedDates?.map {longVal ->
            LocalDate.ofEpochDay(longVal).format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }?.joinToString(", ")
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return schedule.dates?.addedDates?.contains(date.toEpochDay()) ?: false
    }
}