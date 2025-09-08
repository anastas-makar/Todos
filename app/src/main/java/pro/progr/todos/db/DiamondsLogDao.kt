package pro.progr.todos.db

import androidx.room.*

@Dao
interface DiamondsLogDao {

    @Query("SELECT * FROM diamonds_log")
    suspend fun getSync() : List<DiamondsLog>

    @Delete
    suspend fun clearSync(logs: List<DiamondsLog>)

    @Insert
    suspend fun insert(diamondsLog: DiamondsLog)
}