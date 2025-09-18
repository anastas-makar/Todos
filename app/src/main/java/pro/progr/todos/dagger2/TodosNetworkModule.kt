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
import pro.progr.todos.api.TodosNetworkFactory
import javax.inject.Named

@Module
object TodosNetworkModule {

    @Provides @Singleton @Named("baseUrl")
    fun provideBaseUrl(): String = BuildConfig.API_BASE_URL

    @Provides @Singleton @Named("apiKey")
    fun provideApiKey(): String = BuildConfig.API_KEY

    @Provides @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides @Singleton
    fun provideConverterFactory(gson: Gson): Converter.Factory =
        GsonConverterFactory.create(gson)

    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

    @Provides @Singleton
    fun provideApiKeyInterceptor(@Named("apiKey") apiKey: String): Interceptor? =
        apiKey.takeIf { it.isNotBlank() }?.let { key ->
            Interceptor { chain ->
                val old = chain.request()
                val newUrl = old.url.newBuilder()
                    .addQueryParameter("apiKey", key)
                    .build()
                chain.proceed(old.newBuilder().url(newUrl).build())
            }
        }

    @Provides @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        apiKeyInterceptor: Interceptor?
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .apply { apiKeyInterceptor?.let { addInterceptor(it) } }
            .build()

    @Provides @Singleton
    fun provideRetrofit(
        @Named("baseUrl") baseUrl: String,
        okHttp: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit = TodosNetworkFactory.retrofit(baseUrl, okHttp, converterFactory)

    @Provides @Singleton
    fun provideTodosApi(retrofit: Retrofit): TodosApiService =
        retrofit.create(TodosApiService::class.java)
}
