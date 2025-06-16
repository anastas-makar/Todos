package pro.progr.todos.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_tag")
data class NoteTag(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

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