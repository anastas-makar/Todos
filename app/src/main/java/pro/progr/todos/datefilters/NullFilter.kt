package pro.progr.todos.datefilters

import android.content.Context
import pro.progr.todos.R
import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate

class NullFilter : DatesFilter {
    override fun getTitle(context: Context): String {
        return "X"
    }

    override fun getDescription(schedule: Schedule, context: Context): String {
        return context.getString(R.string.not_in_calendar)
    }

    override fun isActual(schedule: Schedule, date: LocalDate): Boolean {
        return false
    }
}
