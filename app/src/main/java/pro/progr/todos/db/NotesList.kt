package pro.progr.doflow.db

import androidx.room.*

@Entity(tableName = "note_lists",
    indices = arrayOf(
        Index(value = arrayOf("sublist_chain"), unique = true)
    ))
@TypeConverters(SublistChainConverter::class)
data class NotesList (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    var title: String,

    @ColumnInfo(name = "is_current")
    var isCurrent : Boolean = false,

    @ColumnInfo(name = "sublist_chain")
    val sublistChain : SublistChain = SublistChain()
)