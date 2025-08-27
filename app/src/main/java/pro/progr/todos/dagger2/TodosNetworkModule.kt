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

@Module
object TodosNetworkModule {

    @Provides @Singleton
    fun provideGson(): Gson = GsonBuilder()
        // .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX") // если нужно
        .create()

    @Provides @Singleton
    fun provideConverterFactory(gson: Gson): Converter.Factory =
        GsonConverterFactory.create(gson)

    @Provides @Singleton
    fun provideOkHttp(
        // подставь интерцепторы при необходимости
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides @Singleton
    fun provideApiKeyInterceptor(/* apiKey: String */): Interceptor =
        Interceptor { chain ->
            val url = chain.request().url.newBuilder()
                .addQueryParameter("apiKey", /* apiKey */ "TEST")
                .build()
            val req = chain.request().newBuilder().url(url).build()
            chain.proceed(req)
        }

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