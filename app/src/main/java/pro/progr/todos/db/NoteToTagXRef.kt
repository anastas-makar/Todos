package pro.progr.todos.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity(
    tableName = "note_to_tag",
    indices = arrayOf(
        Index(value = arrayOf("note_id", "tag_id"), unique = false)
    ))
data class NoteToTagXRef(
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "note_id")
    val noteId: String,

    @ColumnInfo(name = "tag_id")
    val tagId: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),

    @ColumnInfo(defaultValue = "0")
    var deleted : Boolean = false
)