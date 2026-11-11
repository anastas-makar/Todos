package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus

@Entity(
    tableName = "notes_in_history",
    indices = arrayOf(
        Index(value = arrayOf("date", "noteId"), unique = true)
    ))
@TypeConverters(SublistChainConverter::class, ColorStyleConverter::class)
data class NoteInHistory(
    @PrimaryKey(autoGenerate = false)
    var id: String,

    var noteId: String,

    val date: Long,

    var title: String,

    var description: String,

    var reward : Int = 10,

    @ColumnInfo(name = "sublist_chain")
    var sublistChain : SublistChain = SublistChain(),

    var style: ColorStyle,

    var fillTitleBackground: Boolean = true,

    var fillTextBackground: Boolean = true,

    var todo: TodoStatus = TodoStatus.DONE,

    var edited: Boolean = false,

    @ColumnInfo(defaultValue = "0")
    var deleted : Boolean = false
)
