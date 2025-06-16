package pro.progr.todos.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DatesInMonthFilter : DatesFilter {
    override fun getTitle(): String {
        return "Каждый месяц"
    }

    override fun getDescription(schedule: Schedule): String {
        schedule.dates?.addedDates?.sort()
        val formatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        return "Дни каждого месяца: " + schedule.dates?.addedDates?.map { longVal ->
            LocalDate.ofEpochDay(longVal).format(formatter)
        }?.joinToString(", ")
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return schedule.dates?.addedDates?.any { longVal ->
            val scheduleDate = LocalDate.ofEpochDay(longVal)
            scheduleDate.dayOfMonth == date.dayOfMonth
        } ?: false
    }
}