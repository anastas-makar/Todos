package pro.progr.doflow.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate

class NullFilter : DatesFilter {
    override fun getTitle(): String {
        return "X"
    }

    override fun getDescription(schedule: Schedule): String {
        return "Не в календаре"
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return false
    }
}