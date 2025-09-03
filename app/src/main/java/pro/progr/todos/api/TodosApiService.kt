package pro.progr.todos.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TodosApiService {
    @POST("sync/start")
    suspend fun syncStart(@Body payload: TodosSync): Response<TodosSync>

    @POST("sync/finish")
    suspend fun syncFinish(@Body payload: SyncMetaData): Response<Boolean>
}