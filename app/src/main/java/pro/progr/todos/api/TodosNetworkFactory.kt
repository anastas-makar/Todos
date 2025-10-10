package pro.progr.todos.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TodosNetworkFactory {

    fun authHeadersInterceptor(
        deviceIdProvider: () -> String,
        userIdProvider: () -> String
    ) = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("deviceId", deviceIdProvider())
            .header("userId", userIdProvider())
            .build()
        chain.proceed(request)
    }

    fun apiKeyInterceptor(key: String?): Interceptor? =
        key?.takeIf { it.isNotBlank() }?.let { apiKey ->
            Interceptor { chain ->
                val old = chain.request()
                val newUrl = old.url.newBuilder()
                    .addQueryParameter("apiKey", apiKey)
                    .build()
                chain.proceed(old.newBuilder().url(newUrl).build())
            }
        }

    fun loggingInterceptor(isDebug: Boolean) =
        HttpLoggingInterceptor().apply {
            level = if (isDebug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    fun okHttp(
        isDebug: Boolean,
        apiKey: String?,
        deviceIdProvider: () -> String,
        userIdProvider: () -> String
    ): OkHttpClient = OkHttpClient.Builder()
        // сначала заголовки
        .addInterceptor(authHeadersInterceptor(deviceIdProvider, userIdProvider))
        //.apply { apiKeyInterceptor(apiKey)?.let(::addInterceptor) }
        .addInterceptor(loggingInterceptor(isDebug))
        .build()

    fun retrofit(
        baseUrl: String,
        client: OkHttpClient,
        converterFactory: Converter.Factory = GsonConverterFactory.create(GsonBuilder().create())
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(converterFactory)
        .build()

    fun todosApi(
        baseUrl: String,
        isDebug: Boolean,
        apiKey: String?,
        deviceIdProvider: () -> String,
        userIdProvider: () -> String
    ): TodosApiService =
        retrofit(
            baseUrl,
            okHttp(isDebug, apiKey, deviceIdProvider, userIdProvider)
        ).create(TodosApiService::class.java)
}
