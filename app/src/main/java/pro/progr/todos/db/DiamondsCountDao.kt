package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiamondsCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diamondsCount: DiamondsCount) : Long

    @Update
    fun update(diamondsCount: DiamondsCount) : Int

    @Query("SELECT SUM(count) FROM diamonds_count")
    fun getTotal() : Flow<Int>

    @Query("SELECT * FROM diamonds_count")
    fun getAll() : Flow<List<DiamondsCount>?>

    @Query("SELECT count FROM diamonds_count WHERE day = :day")
    fun getDayCount(day: Long) : Flow<Long>

    @Delete
    fun delete(diamondsCount: DiamondsCount)

    @Query("INSERT INTO diamonds_count (day, count) VALUES(:day, :count)")
    fun insertDiamondsCount(day: Long, count : Int)

    @Query("UPDATE diamonds_count SET count = (count + :count) WHERE day = :day")
    fun updateDiamondsCount(day: Long, count : Int) : Int

    @Transaction
    suspend fun addOneToCount(day: Long) {
        if (updateDiamondsCount(day, 1) == 0) {
            insertDiamondsCount(day, 1)
        }
    }

    @Transaction
    suspend fun updateCount(day: Long, count: Int) {
        if (updateDiamondsCount(day, count) == 0) {
            insertDiamondsCount(day, count)
        }
    }
}