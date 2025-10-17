package pro.progr.todos.db

import androidx.room.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

/**
 * В этой таблице хранятся
 * DiamondLog, которые пришли С СЕРВЕРА
 * после выполнения /sync/start
 * и пока успешно не выполнится /sync/finish
 *
 * Это нужно для того, чтобы в случае сбоя /sync/finish
 * два раза не начислились одни и те же DiamondsLog
 *
 * После успешного выполнения /sync/finish
 * на сервере поднимается версия, начиная с которой
 * ищутся данные для этого deviceId,
 * поэтому старые DiamondLogServerReceived
 * можно безопасно удалить -- они не придут с сервера повторно
 */
@Entity(
    tableName = "diamond_logs_server_received"
)
data class DiamondLogServerReceived(
    @PrimaryKey(autoGenerate = false)
    val uuid: String = UUID.randomUUID().toString(),
    val createdAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    var day : Long,
    var count : Int
)