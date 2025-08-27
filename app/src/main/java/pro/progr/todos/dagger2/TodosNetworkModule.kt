package pro.progr.todos.dagger2

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.progr.todos.api.TodosApiService
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import pro.progr.todos.BuildConfig

@Module
object TodosNetworkModule {

    @Provides @Singleton
    fun provideGson(): Gson = GsonBuilder()
        // .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX") // если нужно
        .create()

    @Provides @Singleton
    fun provideConverterFactory(gson: Gson): Converter.Factory =
        GsonConverterFactory.create(gson)

    // 1) Логгер — отдельный провайдер
    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

    // 2) ApiKey — отдельный провайдер (nullable: можно отключить)
    @Provides @Singleton
    fun provideApiKeyInterceptor(): Interceptor? {
        // возьми ключ откуда удобно: BuildConfig, SharedPrefs, и т.п.
        val apiKey = BuildConfig.API_KEY // или null, если не нужен
        if (apiKey.isNullOrBlank()) return null

        return Interceptor { chain ->
            val old = chain.request()
            val newUrl = old.url.newBuilder()
                .addQueryParameter("apiKey", apiKey)
                .build()
            val newReq = old.newBuilder().url(newUrl).build()
            chain.proceed(newReq)
        }
    }

    // 3) OkHttp — инжектим оба интерцептора параметрами
    @Provides @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        apiKeyInterceptor: Interceptor?,   // ← вот «его» и инжектим
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .apply { apiKeyInterceptor?.let { addInterceptor(it) } }
            .build()


    @Provides @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttp: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl) // ДОЛЖЕН заканчиваться на '/'
        .client(okHttp)
        .addConverterFactory(converterFactory)
        .build()

    @Provides @Singleton
    fun provideTodosApi(retrofit: Retrofit): TodosApiService =
        retrofit.create(TodosApiService::class.java)
}