package pro.progr.todos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pro.progr.diamondapi.AccumulateInterface
import java.time.LocalDate
import javax.inject.Inject

class DiamondViewModel @Inject constructor(private val diamondsCountRepository: DiamondsCountRepository) : ViewModel(),
    AccumulateInterface {
    val diamondsCountForDates = mutableStateOf(mapOf<Long, Int>())

    val diamondsFlow = diamondsCountRepository.getAll()

    init {
        viewModelScope.launch (Dispatchers.Default) {
            diamondsFlow.collectLatest { diamondsList ->

                diamondsList?.let {
                    withContext(Dispatchers.Main) {
                        diamondsCountForDates.value = diamondsList.associateBy ({ it.day}, {it.count})
                    }
                }
            }
        }
    }

    override fun getDiamondsCount(): Flow<Int> {
        return diamondsFlow.map { list ->
            list?.let {
                it.sumOf { d -> d.count }
            } ?: 0
        }
    }

    override suspend fun add(diamonds : Int): Result<Boolean> {
        viewModelScope.launch (Dispatchers.Default) {
            diamondsCountRepository.addDiamondForDate(LocalDate.now())
        }

        return Result.success(true)
    }
}