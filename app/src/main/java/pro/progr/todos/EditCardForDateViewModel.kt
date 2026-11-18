package pro.progr.todos

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pro.progr.todos.brightcards.ColorUpdateInterface
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.CardContent
import java.time.LocalDate

class EditCardForDateViewModel(
    private val repository: CardRepository,
    private val id: String,
    private val epochDay: Long
) : ViewModel(), ColorUpdateInterface {
    private val contentState = MutableStateFlow(CardContent(id = id))

    internal val cardContent: MutableState<CardContent> = mutableStateOf(contentState.value)
    private val _editorReady = mutableStateOf(false)
    val editorReady: State<Boolean> get() = _editorReady

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = repository.getCardForDate(id, epochDay)
            contentState.value = loaded
            cardContent.value = loaded
            _editorReady.value = true
        }
    }

    fun updateCardTitle(title: String) = update { it.copy(title = title) }
    fun updateCardDescription(description: String) = update { it.copy(text = description) }

    private fun update(transform: (CardContent) -> CardContent) {
        val updated = transform(contentState.value)
        contentState.value = updated
        cardContent.value = updated
        if (_editorReady.value) persist(updated)
    }

    private fun persist(content: CardContent) {
        repository.enqueueTemplateEdit {
            repository.updateDateEntry(content, epochDay)
        }
    }

    override fun getCardContent(): MutableState<CardContent> = cardContent

    override fun updateCardTitleBackground(filled: Boolean) = update { it.copy(fillTitleBackground = filled) }
    override fun updateCardDescriptionBackground(filled: Boolean) = update { it.copy(fillTextBackground = filled) }
    override fun updateCardStyle(style: ColorStyle) = update { it.copy(style = style) }

    fun removeForDay(date: LocalDate) {
        if (!_editorReady.value) return
        val current = contentState.value
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeForDay(current, date)
        }
    }
}
