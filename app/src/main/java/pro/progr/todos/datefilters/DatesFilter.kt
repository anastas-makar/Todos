package pro.progr.todos.datefilters

import android.content.Context
import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate

interface DatesFilter {
    fun getTitle(context: Context) : String
    fun getDescription(schedule: Schedule, context: Context) : String
    fun isActual(schedule: Schedule, date: LocalDate) : Boolean
}
