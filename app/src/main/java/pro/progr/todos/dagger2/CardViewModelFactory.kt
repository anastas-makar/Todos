package pro.progr.todos.dagger2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.colors.GouachePalette
import pro.progr.todos.CardRepository
import pro.progr.todos.CardViewModel
import pro.progr.todos.EditCardForDateViewModel
import pro.progr.diagnostics.Diagnostics
import javax.inject.Inject

class CardViewModelFactory @Inject constructor(
    private val repository: CardRepository,
    private val diagnostics: Diagnostics
) : ViewModelProvider.Factory {
    var cardId: String? = null
    var style: ColorStyle = GouachePalette.entries.toTypedArray().random()

    //для задач в истории
    var epochDay: Long? = null

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            return if (cardId != null) {
                CardViewModel(repository, cardId!!, diagnostics) as T
            } else {
                CardViewModel(repository, style, diagnostics) as T
            }
        } else if (modelClass.isAssignableFrom(EditCardForDateViewModel::class.java)
            && epochDay != null) {
            val id = cardId ?: throw IllegalArgumentException("A dated entry editor needs a Note id")
            return EditCardForDateViewModel(repository, id, epochDay!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
