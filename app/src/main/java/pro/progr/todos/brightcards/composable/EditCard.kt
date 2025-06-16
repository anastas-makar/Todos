package pro.progr.brightcards.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.brightcards.composable.description.EditDescriptionFilled
import pro.progr.brightcards.composable.description.EditDescriptionTransparent
import pro.progr.brightcards.composable.title.EditTitleFilled
import pro.progr.brightcards.composable.title.EditTitleTransparent
import pro.progr.brightcards.vm.CardViewModel

@Composable
fun EditCard(cardViewModel: CardViewModel) {

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
            .border(
                1.dp,
                Color(cardViewModel.getCardContent().value.style.backgroundColor()),
                RoundedCornerShape(5.dp)
            ) // Добавление границы
            .fillMaxHeight()
    ) {

        if(cardViewModel.getCardContent().value.fillTitleBackground) {
            EditTitleFilled(cardViewModel = cardViewModel)

        } else {
            EditTitleTransparent(cardViewModel = cardViewModel)
        }

        if(cardViewModel.getCardContent().value.fillTextBackground) {
            EditDescriptionFilled(cardViewModel = cardViewModel)
        } else {
            EditDescriptionTransparent(cardViewModel = cardViewModel)
        }

    }
}
