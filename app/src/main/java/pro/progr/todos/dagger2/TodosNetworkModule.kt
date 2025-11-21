package pro.progr.todos.dagger2

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.progr.authapi.AuthInterface
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
    fun provideOkHttp(
        auth: AuthInterface
    ): OkHttpClient = TodosNetworkFactory.okHttp(
        isDebug = true,
        auth = auth
    )

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
