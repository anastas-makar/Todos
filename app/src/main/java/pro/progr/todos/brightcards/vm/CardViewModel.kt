package pro.progr.todos.brightcards.vm

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Job
import pro.progr.todos.brightcards.model.CardContent
import pro.progr.todos.brightcards.model.CardAction

interface CardViewModel {
    fun getCardId() : String?

    fun onCardActionClicked(action: CardAction)

    fun onCardActionClickFired()

    fun getCard(id: String) : Job

    fun deleteCard()

    fun updateCard(content: CardContent)

    fun setDone()

    fun showReward() : Boolean

    fun getCardContent() : MutableState<CardContent>
}
