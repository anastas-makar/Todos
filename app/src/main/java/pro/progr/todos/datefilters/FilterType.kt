package pro.progr.todos.datefilters

enum class FilterType (val datesFilter: DatesFilter) {
    DATE(CalendarDatesFilter()),
    DATE_IN_YEAR(DatesInYearFilter()),
    DATE_IN_MONTH(DatesInMonthFilter()),
    EVERY_DAY(DaysSequenceFilter()),
    DAY_AFTER_DAY(DayAfterDayFilter()),
    DAYS_AFTER_DAYS(DaysAfterDaysFilter()),
    WEEK(WeekDaysFilter()),
    WEEK_IN_MONTH(WeekDaysFilter()),
    WEEK_IN_YEAR(WeekDaysFilter()),
    WEEK_AFTER_WEEK(WeekDaysFilter()),
    NO_TERMS(NullFilter())
    ;
}