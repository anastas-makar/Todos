package pro.progr.todos.db

import androidx.room.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity(tableName = "note_lists",
    indices = arrayOf(
        Index(value = arrayOf("sublist_chain"), unique = true)
    ))
@TypeConverters(SublistChainConverter::class)
data class NotesList (
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(),

    var title: String,

    @ColumnInfo(name = "is_current")
    var isCurrent : Boolean = false,

    @ColumnInfo(name = "sublist_chain")
    val sublistChain : SublistChain = SublistChain(),

    @ColumnInfo(name = "updated_at")
    var updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),

    @ColumnInfo(defaultValue = "0")
    var deleted : Boolean = false
)