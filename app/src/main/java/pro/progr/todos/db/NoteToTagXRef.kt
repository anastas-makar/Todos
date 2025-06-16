package pro.progr.doflow.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_to_tag",
    indices = arrayOf(
        Index(value = arrayOf("note_id", "tag_id"), unique = true)
    ))
data class NoteToTagXRef(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "note_id")
    val noteId: Long,

    @ColumnInfo(name = "tag_id")
    val tagId: Long
)