package pro.progr.todos.brightcards

import androidx.compose.runtime.MutableState
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.CardContent

interface ColorUpdateInterface {
    fun updateCardTitleBackground(
        filled: Boolean
    )

    fun getCardContent(): MutableState<CardContent>

    fun updateCardDescriptionBackground(
        filled: Boolean
    )

    fun updateCardStyle(
        style: ColorStyle
    )
}