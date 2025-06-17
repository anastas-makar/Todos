package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.diamondsandberries.db.ScheduleConverter
import pro.progr.todos.datefilters.FilterType

@Entity(
    tableName = "notes"
)
@TypeConverters(SublistChainConverter::class, ColorStyleConverter::class)
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

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
) {


    @TypeConverters(ScheduleConverter::class)
    var schedule: Schedule = Schedule(
        dates = Schedule.Dates(
            since = dateSince,
            till = dateTill,
            cancelledDates = cancelledDates?.longArray ?: emptyArray(),
            addedDates = addedDates?.longArray ?: emptyArray()
        ),
        pattern = Schedule.Pattern(days = patterDates ?: LinkedHashMap(), type = patternType ?: FilterType.NO_TERMS)
    )
        get () =
            Schedule(
                dates = Schedule.Dates(
                    since = dateSince,
                    till = dateTill,
                    cancelledDates = cancelledDates?.longArray ?: emptyArray(),
                    addedDates = addedDates?.longArray ?: emptyArray()
                ),
                pattern = Schedule.Pattern(days = patterDates ?: LinkedHashMap(), type = patternType ?: FilterType.NO_TERMS)
            )
        set (value) {
            field = value
            addedDates = ArrayPOJO(value.dates?.addedDates ?: emptyArray())
            cancelledDates = ArrayPOJO(value.dates?.cancelledDates ?: emptyArray())
            patterDates = value.pattern.days
            patternType = value.pattern.type
            dateSince = value.dates?.since
            dateTill = value.dates?.till
        }
}