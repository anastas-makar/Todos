package pro.progr.todos.api

import java.time.LocalDateTime
import java.time.ZoneOffset

data class SyncMetaData(
    val currentTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val dbVersion: Int,
    val latestUpdates: HashMap<String, Long> = HashMap()
)