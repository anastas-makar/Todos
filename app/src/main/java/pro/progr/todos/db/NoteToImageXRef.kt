package pro.progr.todos.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_to_image",
    indices = arrayOf(
        Index(value = arrayOf("note_id", "image_id"), unique = true)
    ))
data class NoteToImageXRef(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "note_id")
    val noteId: Long,

    @ColumnInfo(name = "image_id")
    val imageId: Long
)