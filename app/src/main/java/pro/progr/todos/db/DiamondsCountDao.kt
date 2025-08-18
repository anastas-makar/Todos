package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset

@Dao
interface DiamondsCountDao {

    @Query("SELECT * FROM diamonds_count WHERE updated_at > :lastUpdateTime")
    suspend fun getUpdates(lastUpdateTime : Long) : List<DiamondsCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<DiamondsCount>)

    @Query("SELECT SUM(count) FROM diamonds_count")
    fun getTotal() : Flow<Int>

    @Query("SELECT * FROM diamonds_count")
    fun getAll() : Flow<List<DiamondsCount>?>

    @Query("INSERT INTO diamonds_count (day, count, updated_at) VALUES(:day, :count, :updatedAt)")
    fun insertDiamondsCount(day: Long, count : Int,
                            updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    @Query("UPDATE diamonds_count SET count = (count + :count), updated_at = :updatedAt WHERE day = :day")
    fun updateDiamondsCount(day: Long, count : Int,
                            updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) : Int

    @Transaction
    suspend fun updateCount(day: Long, count: Int) {
        if (updateDiamondsCount(day, count) == 0) {
            insertDiamondsCount(day, count)
        }
    }
}