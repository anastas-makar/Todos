package pro.progr.todos.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pro.progr.todos.SyncRepository

class SyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repo: SyncRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result =
        try {
            repo.sync()
            Result.success()
        } catch (e: Throwable) {
            Result.retry()
        }
}