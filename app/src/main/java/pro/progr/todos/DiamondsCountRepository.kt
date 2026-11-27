package pro.progr.todos

import androidx.room.withTransaction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import pro.progr.diamondapi.AccumulateInterface
import pro.progr.diamondapi.ExchangeInterface
import pro.progr.todos.db.DiamondsCount
import pro.progr.todos.db.DiamondsCountDao
import pro.progr.todos.db.DiamondsLog
import pro.progr.todos.db.DiamondsLogDao
import pro.progr.todos.db.TodosDataBase
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiamondsCountRepository
    @Inject constructor(
        val diamondsCountDao: DiamondsCountDao,
        val diamondsLogDao: DiamondsLogDao,
        private val database: TodosDataBase
    ) : ExchangeInterface, AccumulateInterface {
    override fun getDiamondsCount(): Flow<Int> {
        return diamondsCountDao.getTotal()
    }

    override suspend fun add(diamonds: Int): Result<Boolean> = exchangeResult {
        require(diamonds > 0) { "Количество начисляемых бриллиантов должно быть больше нуля" }
        val day = LocalDate.now().toEpochDay()
        database.withTransaction {
            recordDeltaInTransaction(day, diamonds)
        }
        true
    }

    override suspend fun spendDiamonds(diamonds: Int): Result<Boolean> = exchangeResult {
        require(diamonds > 0) { "Количество списываемых бриллиантов должно быть больше нуля" }
        val day = LocalDate.now().toEpochDay()
        database.withTransaction {
            check(diamondsCountDao.getTotalOnce() >= diamonds) {
                "Недостаточно бриллиантов"
            }
            recordDeltaInTransaction(day, -diamonds)
        }
        true
    }

    private suspend fun exchangeResult(block: suspend () -> Boolean): Result<Boolean> = try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        Result.failure(error)
    }

    private suspend fun recordDeltaInTransaction(day: Long, delta: Int) {
        diamondsCountDao.updateCount(day, delta)
        diamondsLogDao.insert(DiamondsLog(day = day, count = delta))
    }

    fun getAll() : Flow<List<DiamondsCount>?> {
        return diamondsCountDao.getAll()
    }

    suspend fun addDiamondsForDate(date: LocalDate, count: Int) {
        val day = date.toEpochDay()
        database.withTransaction {
            recordDeltaInTransaction(day, count)
        }
    }
}
