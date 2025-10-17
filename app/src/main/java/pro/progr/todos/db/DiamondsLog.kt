package pro.progr.todos.db

import androidx.room.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity(
    tableName = "diamonds_log"
)
data class DiamondsLog(
    @PrimaryKey(autoGenerate = false)
    val uuid: String = UUID.randomUUID().toString(),
    val createdAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    var day : Long,
    var count : Int
)