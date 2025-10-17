package pro.progr.todos.db

import androidx.room.*

@Dao
interface DiamondsLogServerReceivedDao {

    @Query("SELECT * FROM diamond_logs_server_received")
    suspend fun getSync() : List<DiamondLogServerReceived>

    @Delete
    suspend fun clearSync(logs: List<DiamondLogServerReceived>)

    @Insert
    suspend fun insert(diamondsLog: List<DiamondLogServerReceived>)
}