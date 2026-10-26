package pro.progr.todos.api.v1

const val CURRENT_SYNC_PROTOCOL_VERSION = 1

data class SyncMetaData(
    val syncId: String,
    val syncProtocolVersion: Int
)
