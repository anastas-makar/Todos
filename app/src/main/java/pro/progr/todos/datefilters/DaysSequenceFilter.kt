package pro.progr.doflow.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate

class DaysSequenceFilter : DatesFilter {
    override fun getTitle(): String {
        return "Каждый день"
    }

    override fun getDescription(schedule: Schedule): String {
        return "Повторять каждый день"
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        if (schedule.dates?.till != null && !date.isBefore(LocalDate.ofEpochDay(schedule.dates!!.till!!))) {
            return false
        }

        if (schedule.dates?.since != null && date.isBefore(LocalDate.ofEpochDay(schedule.dates!!.since!!))) {
            return false
        }

        return true
    }
}