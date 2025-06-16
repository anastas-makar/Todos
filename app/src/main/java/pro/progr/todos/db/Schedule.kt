package pro.progr.diamondsandberries.db

import pro.progr.todos.datefilters.FilterType

@Deprecated(message = "В классе Note отдельные поля")
class Schedule(
    val pattern: Pattern,
    var dates : Dates? = null
) {
    class Dates(
        var since: Long? = null,
        var till: Long? = null,
        val cancelledDates: Array<Long> = emptyArray(),
        val addedDates: Array<Long> = emptyArray()
    )

    class Pattern(
        val days: LinkedHashMap<Long, Day>,
        var type: FilterType
    )

    class Day(
        val longValue: Long,
        val active: Boolean = true
    )

}