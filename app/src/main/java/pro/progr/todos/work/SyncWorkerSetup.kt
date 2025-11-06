package pro.progr.todos.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncWorkerSetup {
    inline fun <reified T> enqueueBackgroundSync(context: Context) where T : ListenableWorker {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val req = OneTimeWorkRequestBuilder<T>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag("bg_sync")
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            "bg_sync",
            ExistingWorkPolicy.KEEP, // уже стоит — второй не запускать
            req
        )
    }
}