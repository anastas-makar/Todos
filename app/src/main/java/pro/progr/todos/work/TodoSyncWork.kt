package pro.progr.todos.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import pro.progr.authapi.AuthInterface
import pro.progr.todos.BuildConfig
import pro.progr.todos.SyncRepository
import pro.progr.todos.api.TodosNetworkFactory
import pro.progr.todos.db.TodosDataBase

suspend fun doTodoSyncWork(applicationContext: Context,
                           auth: AuthInterface): ListenableWorker.Result = try {
    val api = TodosNetworkFactory.todosApi(
        baseUrl = BuildConfig.API_BASE_URL,
        isDebug = BuildConfig.DEBUG,
        auth = auth
    )

    val db = TodosDataBase.getDatabase(applicationContext)
    val syncRepository = SyncRepository(db, api)

    val sid = auth.getSessionId()
    if (sid.isNullOrBlank()) {
        syncRepository.shrink()
    } else {
        syncRepository.sync()
    }

    ListenableWorker.Result.success()

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
