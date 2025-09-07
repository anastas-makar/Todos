package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiamondsCountDao {

    @Query("SELECT IFNULL(SUM(count), 0) FROM diamonds_count")
    fun getTotal() : Flow<Int>

    @Query("SELECT * FROM diamonds_count")
    fun getAll() : Flow<List<DiamondsCount>?>

    @Insert
    fun insertDiamondsCount(count: DiamondsCount)

    @Query("UPDATE diamonds_count SET count = (count + :count) WHERE day = :day")
    fun updateDiamondsCount(day: Long, count : Int) : Int

    suspend fun updateCount(day: Long, count: Int) {
        if (updateDiamondsCount(day, count) == 0) {
            insertDiamondsCount(
                DiamondsCount(day = day,
                    count = count))
        }
    }

    @Transaction
    suspend fun setUpdates(diamondLogs: List<DiamondsLog>) {
        if (diamondLogs.isEmpty()) return
        // группируем по дню и суммируем
        val totalsByDay = diamondLogs.groupBy { it.day }
            .mapValues { entry -> entry.value.sumOf { it.count } }
        for ((day, delta) in totalsByDay) {
            if (delta != 0) updateCount(day, delta)
        }
    }
}