package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.datefilters.FilterType
import java.util.UUID

@Entity(
    tableName = "notes"
)
@TypeConverters(SublistChainConverter::class, ColorStyleConverter::class)
data class Note(
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(),

    val date: Long,

    var title: String,

    var description: String,

    @ColumnInfo(name = "sublist_chain")
    var sublistChain: SublistChain = SublistChain(),

    var reward: Int = 10,

    @TypeConverters(LongArrayConverter::class)
    @ColumnInfo(name = "added_dates")
    var addedDates: ArrayPOJO? = ArrayPOJO(emptyArray()),

    @TypeConverters(LongArrayConverter::class)
    @ColumnInfo(name = "cancelled_dates")
    var cancelledDates: ArrayPOJO? = ArrayPOJO(emptyArray()),

    @ColumnInfo(name = "pattern_days")
    @TypeConverters(PatternDatesConverter::class)
    var patterDates: LinkedHashMap<Long, Schedule.Day>? = LinkedHashMap(),

    @ColumnInfo(name = "pattern_type")
    @TypeConverters(FilterTypeConverter::class)
    var patternType: FilterType? = FilterType.NO_TERMS,

    @ColumnInfo(name = "date_since")
    var dateSince: Long? = null,

    @ColumnInfo(name = "date_till")
    var dateTill: Long? = null,

    var style: ColorStyle,

    var fillTitleBackground: Boolean = true,

    var fillTextBackground: Boolean = true,

    var todo: TodoStatus = TodoStatus.NOT_ACTIVE,

    var latestDone: Long? = null,

    @ColumnInfo(defaultValue = "0")
    var deleted : Boolean = false
)

/** Builds the calendar UI model from the persisted, typed schedule columns. */
fun Note.toSchedule(): Schedule = Schedule(
    dates = Schedule.Dates(
        since = dateSince,
        till = dateTill,
        cancelledDates = cancelledDates?.longArray ?: emptyArray(),
        addedDates = addedDates?.longArray ?: emptyArray()
    ),
    pattern = Schedule.Pattern(
        days = patterDates ?: LinkedHashMap(),
        type = patternType ?: FilterType.NO_TERMS
    )
)

/** Copies a schedule selected in the UI into the persisted, typed columns. */
fun Note.applySchedule(schedule: Schedule) {
    addedDates = ArrayPOJO(schedule.dates?.addedDates ?: emptyArray())
    cancelledDates = ArrayPOJO(schedule.dates?.cancelledDates ?: emptyArray())
    patterDates = schedule.pattern.days
    patternType = schedule.pattern.type
    dateSince = schedule.dates?.since
    dateTill = schedule.dates?.till
}
