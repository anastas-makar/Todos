package pro.progr.todos.db

import androidx.room.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "diamonds_count"
)
@TypeConverters(SublistChainConverter::class, ColorStyleConverter::class)
data class DiamondsCount(
    @PrimaryKey(autoGenerate = false)
    var day : Long,
    var count : Int,
    @ColumnInfo(name = "updated_at")
    var updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)