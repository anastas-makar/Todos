package pro.progr.todos.work

import android.content.Context
import android.util.Log
import java.io.IOException
import androidx.work.ListenableWorker
import androidx.work.Data
import kotlinx.coroutines.CancellationException
import pro.progr.diagnostics.Diagnostics
import pro.progr.diagnostics.NoOpDiagnostics
import pro.progr.todos.api.SyncApiException
import pro.progr.authapi.AuthInterface
import pro.progr.todos.BuildConfig
import pro.progr.todos.SyncRepository
import pro.progr.todos.TagsRepository
import pro.progr.todos.api.TodosNetworkFactory
import pro.progr.todos.db.TodosDataBase
import pro.progr.personalcrypto.PersonalCrypto

suspend fun doTodoSyncWork(
    applicationContext: Context,
    auth: AuthInterface,
    personalCrypto: PersonalCrypto
): ListenableWorker.Result = doTodoSyncWork(
    applicationContext,
    auth,
    personalCrypto,
    NoOpDiagnostics
)

suspend fun doTodoSyncWork(
    applicationContext: Context,
    auth: AuthInterface,
    personalCrypto: PersonalCrypto,
    diagnostics: Diagnostics
): ListenableWorker.Result = try {
    diagnostics.breadcrumb("todos_sync: background_started")
    val api = TodosNetworkFactory.todosApi(
        baseUrl = BuildConfig.API_BASE_URL,
        isDebug = BuildConfig.DEBUG,
        auth = auth
    )

    val db = TodosDataBase.getDatabase(applicationContext)
    val syncRepository = SyncRepository(db, api, personalCrypto, TagsRepository(db), diagnostics)

    val sid = auth.getSessionId()
    if (sid.isNullOrBlank()) {
        syncRepository.shrink()
    } else {
        syncRepository.sync()
    }

    diagnostics.breadcrumb("todos_sync: background_succeeded")
    ListenableWorker.Result.success()

} catch (e: CancellationException) {
    diagnostics.breadcrumb("todos_sync: background_cancelled")
    throw e
} catch (e: SyncApiException) {
    if (e.errorCode == "APP_UPDATE_REQUIRED") {
        diagnostics.breadcrumb("todos_sync: background_update_required status=${e.httpStatus}")
        Log.w("doTodoSyncWork", "Sync protocol is no longer supported; app update required")
        val minimumVersion = e.minimumSyncProtocolVersion
        if (!SyncUpdateRequiredStore.record(applicationContext, minimumVersion)) {
            Log.e("doTodoSyncWork", "Could not persist app update requirement")
        }
        ListenableWorker.Result.failure(
            Data.Builder()
                .putString(SYNC_ERROR_CODE_KEY, e.errorCode)
                .putString(SYNC_USER_MESSAGE_KEY, "Обновите приложение")
                .putInt(SYNC_MIN_PROTOCOL_VERSION_KEY, minimumVersion ?: -1)
                .build()
        )
    } else if (e.httpStatus == 401 || e.httpStatus == 403) {
        diagnostics.breadcrumb("todos_sync: background_unauthorized status=${e.httpStatus}")
        Log.d("doTodoSyncWork", "isUnauthorizedHttp", e)
        ListenableWorker.Result.success()
    } else {
        diagnostics.breadcrumb("todos_sync: background_api_error status=${e.httpStatus}")
        if (e.httpStatus >= 500) {
            diagnostics.reportNonFatal(e, issueKey = "todos_sync_server_error")
        }
        Log.e("doTodoSyncWork", "sync_api_failure status=${e.httpStatus}")
        ListenableWorker.Result.retry()
    }
} catch (e: IOException) {
    diagnostics.breadcrumb("todos_sync: background_network_error")
    Log.w("doTodoSyncWork", "Network error during sync", e)
    ListenableWorker.Result.retry()
} catch (e: Exception) {
    // 401 → очисти сессию и верни success, чтобы не зациклиться
    if (e.isUnauthorizedHttp()) {
        diagnostics.breadcrumb("todos_sync: background_unauthorized")
        Log.d("doTodoSyncWork", "isUnauthorizedHttp",
            e)
        // очистка по месту, если есть доступ
        ListenableWorker.Result.success()
    } else {
        diagnostics.breadcrumb("todos_sync: background_unexpected_error type=${e.javaClass.simpleName}")
        diagnostics.reportNonFatal(e, issueKey = "todos_sync_background_unexpected")
        Log.e("doTodoSyncWork", "Unexpected error during sync", e)
        ListenableWorker.Result.retry()
    }
}

// утилита
private fun Throwable.isUnauthorizedHttp(): Boolean =
    (this as? retrofit2.HttpException)?.code() == 401

const val SYNC_ERROR_CODE_KEY = "sync_error_code"
const val SYNC_USER_MESSAGE_KEY = "sync_user_message"
const val SYNC_MIN_PROTOCOL_VERSION_KEY = "minimum_sync_protocol_version"
