package pro.progr.todos.brightcards

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import pro.progr.todos.brightcards.model.CardContent

class CardStyle(private val card: MutableState<CardContent>) {
    val titleTextColor : Color
        @Composable
        get() = if (card.value.fillTitleBackground)
            Color(card.value.style.textColor())
            else MaterialTheme.colors.onSurface

    val titleBackgroundColor : Color
        @Composable
        get() = if (card.value.fillTitleBackground)
            Color(card.value.style.backgroundColor())
            else MaterialTheme.colors.surface
    val descriptionTextColor : Color
        @Composable
        get() = if (card.value.fillTextBackground)
            Color(card.value.style.textColor())
            else MaterialTheme.colors.onSurface

    val descriptionBackgroundColor : Color
        @Composable
        get() = if (card.value.fillTextBackground)
            Color(card.value.style.backgroundColor())
            else MaterialTheme.colors.surface
}