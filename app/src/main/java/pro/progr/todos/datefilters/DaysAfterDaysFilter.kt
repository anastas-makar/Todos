package pro.progr.todos.datefilters

import pro.progr.diamondsandberries.db.Schedule
import java.time.Duration
import java.time.LocalDate

class DaysAfterDaysFilter : DatesFilter {
    override fun getTitle(): String {
        return "Своё расписание"
    }

    override fun getDescription(schedule: Schedule): String {
        return "Повторять ${schedule.pattern.days[0]?.longValue} через ${schedule.pattern.days[1]?.longValue}"
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

        val patternLength = getPatternLength(schedule.pattern.days)

        if (patternLength == 0L) {
            return true
        }

        val datesPointOnSchedule =
            datesBetween.toDays() % patternLength

        return isActiveByDatePoint(datesPointOnSchedule, schedule.pattern.days)
    }

    private fun getPatternLength(days : HashMap<Long, Schedule.Day>) : Long {
        var daysCount : Long = 0

        for (day in days) {
            daysCount += day.value.longValue
        }

        return daysCount
    }

    private fun isActiveByDatePoint(point : Long, days : HashMap<Long, Schedule.Day>) : Boolean {
        var daysCount : Long = 0

        for (day in days) {
            daysCount += day.value.longValue

            if (daysCount > point) {
                return day.value.active
            }
        }

        return false
    }
}