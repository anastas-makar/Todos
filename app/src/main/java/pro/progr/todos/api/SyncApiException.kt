package pro.progr.todos.api

data class SyncApiErrorResponse(
    val code: String? = null,
    val message: String? = null,
    val minimumSyncProtocolVersion: Int? = null
)

class SyncApiException(
    val httpStatus: Int,
    val errorCode: String?,
    val minimumSyncProtocolVersion: Int? = null
) : RuntimeException("Sync API request failed: HTTP $httpStatus, code=$errorCode")
