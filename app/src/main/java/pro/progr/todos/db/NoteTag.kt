package pro.progr.todos.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity(
    tableName = "note_tag")
data class NoteTag(
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(),

    val title: String,

    @ColumnInfo(name = "updated_at")
    var updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),

    @ColumnInfo(defaultValue = "0")
    var deleted : Boolean = false
) {


    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is NoteTag
                && this.id.equals(other.id)
                && this.title.equals(other.title)
    }
}