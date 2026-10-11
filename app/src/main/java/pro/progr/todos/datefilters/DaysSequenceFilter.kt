package pro.progr.todos.datefilters

import android.content.Context
import pro.progr.todos.R
import pro.progr.diamondsandberries.db.Schedule
import java.time.LocalDate

class DaysSequenceFilter : DatesFilter {
    override fun getTitle(context: Context): String {
        return context.getString(R.string.every_day)
    }

    override fun getDescription(schedule: Schedule, context: Context): String {
        return context.getString(R.string.repeat_daily)
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
