package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.diamondsandberries.db.ScheduleConverter
import pro.progr.todos.datefilters.FilterType

@Entity(
    tableName = "notes_in_history",
    indices = arrayOf(
        Index(value = arrayOf("date", "noteId"), unique = true)
    ))
@TypeConverters(SublistChainConverter::class, ScheduleConverter::class, ColorStyleConverter::class)
data class NoteInHistory(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var noteId: Long,

    val date: Long,

    var title: String,

    var description: String,

    var reward : Int = 10,

    @ColumnInfo(name = "sublist_chain")
    var sublistChain : SublistChain = SublistChain(),

    val schedule: Schedule = Schedule(Schedule.Pattern(LinkedHashMap<Long, Schedule.Day>(0), FilterType.DATE)),

    var style: ColorStyle,

    var fillTitleBackground: Boolean = true,

    var fillTextBackground: Boolean = true,

    var todo: TodoStatus = TodoStatus.DONE,

    var edited: Boolean = false
)