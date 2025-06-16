package pro.progr.todos.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.Duration
import java.time.LocalDate

class DayAfterDayFilter : DatesFilter {
    override fun getTitle(): String {
        return "Через день"
    }

    override fun getDescription(schedule: Schedule): String {
        return "Повторять через день"
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        if (schedule.dates?.till != null && !date.isBefore(LocalDate.ofEpochDay(schedule.dates!!.till!!))) {
            return false
        }

        if (schedule.dates?.since != null && date.isBefore(LocalDate.ofEpochDay(schedule.dates!!.since!!))) {
            return false
        }

        val datesBetween = Duration.between(LocalDate.ofEpochDay(schedule.dates?.since ?: LocalDate.now().toEpochDay()).atStartOfDay(), date.atStartOfDay())

        if (datesBetween.isNegative) {
            return false
        }


        return datesBetween.toDays() % 2 == 0L
    }
}