package pro.progr.doflow.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_image")
data class NoteImage(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "image_name", defaultValue = "")
    var imageName : String = ""
)