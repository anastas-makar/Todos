package pro.progr.todos.brightcards.vm

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Job
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.StringAnnotation

interface CardViewModel {
    fun getCardId() : String?

    fun onAnnotationClicked(annotation: StringAnnotation)

    fun onAnnotationClickFired()

    fun getCard(id: String) : Job

    fun deleteCard()

    fun updateCard(content: CardContent)

    fun setDone()

    fun showReward() : Boolean

    fun getCardContent() : MutableState<CardContent>
}