package pro.progr.doflow

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import pro.progr.doflow.dagger2.DaggerDoFlowComponent
import pro.progr.doflow.dagger2.DoFlowComponent
import pro.progr.doflow.worker.HistoryWorker
import pro.progr.owlgame.worker.GameWorkerSetup
import java.util.concurrent.TimeUnit

class DoFlow : Application() {

    val appComponent: DoFlowComponent by lazy {
        DaggerDoFlowComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)

/*        val workRequest = PeriodicWorkRequestBuilder<HistoryWorker>(8, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dailyWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )*/

        //запуск workmanager для игрового модуля
        GameWorkerSetup.scheduleWork(baseContext)
    }
}