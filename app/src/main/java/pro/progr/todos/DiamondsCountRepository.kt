package pro.progr.doflow

import kotlinx.coroutines.flow.Flow
import pro.progr.diamondapi.PurchaseInterface
import pro.progr.doflow.db.DiamondsCount
import pro.progr.doflow.db.DiamondsCountDao
import java.time.LocalDate
import javax.inject.Inject

class DiamondsCountRepository
    @Inject constructor(val diamondsCountDao: DiamondsCountDao) : PurchaseInterface {
    override fun getDiamondsCount(): Flow<Int> {
        return diamondsCountDao.getTotal()
    }

    override suspend fun spendDiamonds(diamonds: Int): Result<Boolean> {

        diamondsCountDao.updateCount(LocalDate.now().toEpochDay(), -diamonds)

        return Result.success(true)
    }

    fun getTotal() : Flow<Int?> {
        return diamondsCountDao.getTotal()
    }

    fun getAll() : Flow<List<DiamondsCount>?> {
        return diamondsCountDao.getAll()
    }

    suspend fun addDiamondForDate(date: LocalDate) {
        diamondsCountDao.addOneToCount(date.toEpochDay())
    }
}