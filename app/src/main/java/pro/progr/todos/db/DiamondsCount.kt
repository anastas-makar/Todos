package pro.progr.todos.db

import androidx.room.*

@Entity(
    tableName = "diamonds_count"
)
@TypeConverters(SublistChainConverter::class, ColorStyleConverter::class)
data class DiamondsCount(
    @PrimaryKey(autoGenerate = false)
    var day : Long,
    var count : Int
)