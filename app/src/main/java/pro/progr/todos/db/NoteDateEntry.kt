package pro.progr.todos.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus

/** A user-created snapshot of one Note on one calendar date. */
@Entity(
    tableName = "note_date_entries",
    indices = [Index(value = ["noteId", "date"], unique = true)]
)
@TypeConverters(ColorStyleConverter::class)
data class NoteDateEntry(
    @PrimaryKey(autoGenerate = false) val id: String,
    val noteId: String,
    val date: Long,
    val title: String,
    val description: String,
    val style: ColorStyle,
    val fillTitleBackground: Boolean,
    val fillTextBackground: Boolean,
    val todo: TodoStatus,
    @ColumnInfo(defaultValue = "0") val deleted: Boolean = false
)
