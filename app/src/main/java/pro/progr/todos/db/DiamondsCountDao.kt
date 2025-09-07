package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiamondsCountDao {

    //todo: с сервера приходит сумма обновления для каждого дня, применяется сложение
    @Query("SELECT SUM(count) FROM diamonds_count")
    fun getTotal() : Flow<Int>

    @Query("SELECT * FROM diamonds_count")
    fun getAll() : Flow<List<DiamondsCount>?>

    @Query("INSERT INTO diamonds_count (day, count) VALUES(:day, :count)")
    fun insertDiamondsCount(day: Long, count : Int)

    @Query("UPDATE diamonds_count SET count = (count + :count) WHERE day = :day")
    fun updateDiamondsCount(day: Long, count : Int) : Int

    @Transaction
    suspend fun updateCount(day: Long, count: Int) {
        if (updateDiamondsCount(day, count) == 0) {
            insertDiamondsCount(day, count)
        }
    }

    suspend fun setUpdates(diamondLogs: List<DiamondsLog>) {
        //todo: логи сгруппироват по дням, взять сумму для каждого дня, прибавить сумму к каждому дню
    }
}