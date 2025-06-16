package pro.progr.brightcards.vm

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Job
import pro.progr.brightcards.model.CardContent
import pro.progr.brightcards.model.StringAnnotation

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