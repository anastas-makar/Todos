package pro.progr.todos.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pro.progr.diamondapi.AuthInterface
import pro.progr.todos.BuildConfig
import pro.progr.todos.SyncRepository
import pro.progr.todos.api.TodosNetworkFactory
import pro.progr.todos.db.TodosDataBase

class SyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val auth: AuthInterface
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = try {
        val sid = auth.getSessionId()
        if (!sid.isNullOrBlank()) {

            val api = TodosNetworkFactory.todosApi(
                baseUrl = BuildConfig.API_BASE_URL,
                isDebug = BuildConfig.DEBUG,
                deviceIdProvider = { auth.getDeviceId() },
                sessionIdProvider = { auth.getSessionId().orEmpty() } // не запирайся на старом sid
            )

            val db = TodosDataBase.getDatabase(applicationContext)
            SyncRepository(db, api).sync()
        }

        Result.success()

    } catch (e: Throwable) {
        // 401 → очисти сессию и верни success, чтобы не зациклиться
        if (e.isUnauthorizedHttp()) {
            // очистка по месту, если есть доступ
            Result.success()
        } else {
            Result.retry()
        }
    }
}

// утилита
private fun Throwable.isUnauthorizedHttp(): Boolean =
    (this as? retrofit2.HttpException)?.code() == 401
