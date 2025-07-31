package pro.progr.todos.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "note_tag")
data class NoteTag(
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(),

    val title: String
) {


    override fun hashCode(): Int {
        return id.toInt()
    }

    override fun equals(other: Any?): Boolean {
        return other is NoteTag
                && this.id.equals(other.id)
                && this.title.equals(other.title)
    }
}