package pro.progr.todos.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Data
import pro.progr.todos.api.SyncApiException
import pro.progr.authapi.AuthInterface
import pro.progr.todos.BuildConfig
import pro.progr.todos.SyncRepository
import pro.progr.todos.api.TodosNetworkFactory
import pro.progr.todos.db.TodosDataBase
import pro.progr.personalcrypto.PersonalCrypto

suspend fun doTodoSyncWork(applicationContext: Context,
                           auth: AuthInterface,
                           personalCrypto: PersonalCrypto): ListenableWorker.Result = try {
    val api = TodosNetworkFactory.todosApi(
        baseUrl = BuildConfig.API_BASE_URL,
        isDebug = BuildConfig.DEBUG,
        auth = auth
    )

    val db = TodosDataBase.getDatabase(applicationContext)
    val syncRepository = SyncRepository(db, api, personalCrypto)

    val sid = auth.getSessionId()
    if (sid.isNullOrBlank()) {
        syncRepository.shrink()
    } else {
        syncRepository.sync()
    }

    ListenableWorker.Result.success()

} catch (e: SyncApiException) {
    if (e.errorCode == "APP_UPDATE_REQUIRED") {
        Log.w("doTodoSyncWork", "Sync protocol is no longer supported; app update required")
        ListenableWorker.Result.failure(
            Data.Builder()
                .putString(SYNC_ERROR_CODE_KEY, e.errorCode)
                .putString(SYNC_USER_MESSAGE_KEY, "Обновите приложение")
                .putInt(SYNC_MIN_PROTOCOL_VERSION_KEY, e.minimumSyncProtocolVersion ?: -1)
                .build()
        )
    } else if (e.httpStatus == 401 || e.httpStatus == 403) {
        Log.d("doTodoSyncWork", "isUnauthorizedHttp", e)
        ListenableWorker.Result.success()
    } else {
        Log.e("doTodoSyncWork", "sync_api_failure status=${e.httpStatus} code=${e.errorCode}")
        ListenableWorker.Result.retry()
    }
} catch (e: Throwable) {
    // 401 → очисти сессию и верни success, чтобы не зациклиться
    if (e.isUnauthorizedHttp()) {
        Log.d("doTodoSyncWork", "isUnauthorizedHttp",
            e)
        // очистка по месту, если есть доступ
        ListenableWorker.Result.success()
    } else {
        Log.e("doTodoSyncWork", e.message, e)

        ListenableWorker.Result.retry()
    }
}

// утилита
private fun Throwable.isUnauthorizedHttp(): Boolean =
    (this as? retrofit2.HttpException)?.code() == 401

const val SYNC_ERROR_CODE_KEY = "sync_error_code"
const val SYNC_USER_MESSAGE_KEY = "sync_user_message"
const val SYNC_MIN_PROTOCOL_VERSION_KEY = "minimum_sync_protocol_version"
