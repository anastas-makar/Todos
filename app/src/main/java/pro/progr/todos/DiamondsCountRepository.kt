package pro.progr.todos

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pro.progr.diamondapi.PurchaseInterface
import pro.progr.todos.db.DiamondsCount
import pro.progr.todos.db.DiamondsCountDao
import pro.progr.todos.db.DiamondsLog
import pro.progr.todos.db.DiamondsLogDao
import java.time.LocalDate
import javax.inject.Inject

class DiamondsCountRepository
    @Inject constructor(
        val diamondsCountDao: DiamondsCountDao,
        val diamondsLogDao: DiamondsLogDao) : PurchaseInterface {
    override fun getDiamondsCount(): Flow<Int> {
        return diamondsCountDao.getTotal()
    }

    @Transaction
    override suspend fun spendDiamonds(diamonds: Int): Result<Boolean> {
        val day =LocalDate.now().toEpochDay()
        val count = -diamonds

        diamondsCountDao.updateCount(day, count)
        diamondsLogDao.insert(DiamondsLog(
            day = day,
            count = count
        ))

        return Result.success(true)
    }

    fun getAll() : Flow<List<DiamondsCount>?> {
        return diamondsCountDao.getAll()
    }

    @Transaction
    suspend fun addDiamondsForDate(date: LocalDate, count: Int) {
        val day = date.toEpochDay()
        diamondsCountDao.updateCount(day, count)
        diamondsLogDao.insert(DiamondsLog(
            day = day,
            count = count
        ))
    }
}