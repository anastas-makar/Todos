package pro.progr.doflow.dagger2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.progr.brightcards.colors.ColorStyle
import pro.progr.brightcards.colors.GouachePalette
import pro.progr.doflow.CardRepository
import pro.progr.doflow.CardViewModel
import pro.progr.doflow.EditCardInHistoryViewModel
import javax.inject.Inject

class CardViewModelFactory @Inject constructor(
    private val repository: CardRepository
) : ViewModelProvider.Factory {
    var cardId: String? = null
    var style: ColorStyle = GouachePalette.entries.toTypedArray().random()

    //для задач в истории
    var epochDay: Long? = null

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            return if (cardId != null) {
                CardViewModel(repository, cardId!!) as T
            } else {
                CardViewModel(repository, style) as T
            }
        } else if (modelClass.isAssignableFrom(EditCardInHistoryViewModel::class.java)
            && epochDay != null) {
            return if (cardId != null) {
                EditCardInHistoryViewModel(repository, cardId!!, epochDay!!) as T
            } else {
                EditCardInHistoryViewModel(repository, style, epochDay!!) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}