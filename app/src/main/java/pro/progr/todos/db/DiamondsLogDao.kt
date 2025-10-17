package pro.progr.todos.db

import androidx.room.*

@Dao
interface DiamondsLogDao {

    @Query("SELECT * FROM diamonds_log")
    suspend fun getAll() : List<DiamondsLog>

    @Delete
    suspend fun clear(logs: List<DiamondsLog>)

    @Insert
    suspend fun insert(diamondsLog: DiamondsLog)

    @Insert
    suspend fun insert(diamondsLog: List<DiamondsLog>)
}