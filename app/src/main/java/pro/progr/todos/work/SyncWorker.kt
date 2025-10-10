package pro.progr.todos.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pro.progr.todos.BuildConfig
import pro.progr.todos.SyncRepository
import pro.progr.todos.api.TodosNetworkFactory
import pro.progr.todos.db.TodosDataBase
import pro.progr.todos.util.DeviceIdProvider

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result =
        try {

            DeviceIdProvider.init(applicationContext)

            val api = TodosNetworkFactory.todosApi(
                baseUrl = BuildConfig.API_BASE_URL,
                isDebug = true,
                apiKey  = BuildConfig.API_KEY,
                deviceIdProvider = { DeviceIdProvider.get() },
                userIdProvider = { "testuserid" }
            )

            val db = TodosDataBase.getDatabase(applicationContext)
            SyncRepository(db, api).sync()
            Result.success()
        } catch (e: Throwable) {
            Result.retry()
        }

}