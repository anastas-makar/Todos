package pro.progr.todos.db

import androidx.room.*

@Dao
interface DiamondsLogDao {

    @Query("SELECT * FROM diamonds_log")
    fun getSync() : List<DiamondsLog>

    @Delete
    fun clearSync(logs: List<DiamondsLog>)

    @Insert
    fun insert(diamondsLog: DiamondsLog)
}