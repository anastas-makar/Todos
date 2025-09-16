package pro.progr.todos.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.progr.todos.SyncRepository
import pro.progr.todos.api.TodosApiService
import pro.progr.todos.db.TodosDataBase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pro.progr.todos.BuildConfig

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result =
        try {
            val repo = SyncRepository(db = TodosDataBase.getDatabase(context = applicationContext),
                buildTodosApi(
                    baseUrl = BuildConfig.API_BASE_URL,
                    apiKey  = BuildConfig.API_KEY,
                    isDebug = BuildConfig.DEBUG
                )
            )
            repo.sync()
            Result.success()
        } catch (e: Throwable) {
            Result.retry()
        }

    private fun buildTodosApi(baseUrl: String, apiKey: String?, isDebug: Boolean): TodosApiService {
        val gson = GsonBuilder().create()

        val logging = HttpLoggingInterceptor().apply {
            level = if (isDebug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val apiKeyInterceptor = apiKey?.takeIf { it.isNotBlank() }?.let { key ->
            Interceptor { chain ->
                val old = chain.request()
                val newUrl = old.url.newBuilder()
                    .addQueryParameter("apiKey", key)
                    .build()
                chain.proceed(old.newBuilder().url(newUrl).build())
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .apply { if (apiKeyInterceptor != null) addInterceptor(apiKeyInterceptor) }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl) // важно: с '/' на конце
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(TodosApiService::class.java)
    }
}