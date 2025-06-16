package pro.progr.todos.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate

interface DatesFilter {
    fun getTitle() : String
    fun getDescription(schedule: Schedule) : String
    fun isActual(schedule: Schedule, date: LocalDate) : Boolean
}