package pro.progr.todos.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_to_note",
    indices = arrayOf(
            Index(value = arrayOf("note_to"))
            )
    )
data class NoteToNote(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "note_to")
    val noteTo: Long,

    @ColumnInfo(name = "note_from")
    val noteFrom: Long,

    val comment: String
)