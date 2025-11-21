package pro.progr.todos.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.progr.authapi.AuthInterface
import pro.progr.authapi.UnauthorizedInterceptor
import pro.progr.authapi.signingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TodosNetworkFactory {

    fun loggingInterceptor(isDebug: Boolean) =
        HttpLoggingInterceptor().apply {
            level = if (isDebug) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    fun okHttp(
        isDebug: Boolean,
        auth: AuthInterface
    ): OkHttpClient = OkHttpClient.Builder()
        // сначала подписание
        .addInterceptor(signingInterceptor(auth))
        // потом логирование, чтобы лог видел уже подписанные заголовки
        .addInterceptor(loggingInterceptor(isDebug))
        .addInterceptor(UnauthorizedInterceptor(auth))
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
        auth: AuthInterface
    ): TodosApiService =
        retrofit(
            baseUrl,
            okHttp(isDebug, auth)
        ).create(TodosApiService::class.java)
}

