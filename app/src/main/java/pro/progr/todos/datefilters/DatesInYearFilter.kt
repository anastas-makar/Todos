package pro.progr.doflow.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DatesInYearFilter : DatesFilter {
    override fun getTitle(): String {
        return "Каждый год"
    }

    override fun getDescription(schedule: Schedule): String {
        schedule.dates?.addedDates?.sort()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
        return "Даты каждого года: " + schedule.dates?.addedDates?.map { longVal ->
            LocalDate.ofEpochDay(longVal).format(formatter)
        }?.joinToString(", ")
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return schedule.dates?.addedDates?.any { longVal ->
            val scheduleDate = LocalDate.ofEpochDay(longVal)
            scheduleDate.dayOfMonth == date.dayOfMonth && scheduleDate.month == date.month
        } ?: false
    }
}