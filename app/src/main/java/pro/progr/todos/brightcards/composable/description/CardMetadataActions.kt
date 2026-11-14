package pro.progr.todos.brightcards.composable.description

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.composable.DiamondSelector
import pro.progr.todos.brightcards.model.CardAction
import pro.progr.todos.CardViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardMetadataActions(cardViewModel: CardViewModel) {
    val content = cardViewModel.getCardContent().value
    FlowRow(
        modifier = Modifier.padding(horizontal = 11.dp),
    ) {

        if (cardViewModel.showReward()) {
            DiamondSelector(cardViewModel)
        }

        listOf(
            content.scheduleLabel to CardAction.EDIT_SCHEDULE,
            content.listLabel to CardAction.SELECT_LIST,
            content.tagsLabel to CardAction.SELECT_TAGS
        ).filter { (label, _) -> label.isNotBlank() }.forEach { (label, action) ->
            val borderColor = if (cardViewModel.getCardContent().value.fillTextBackground)
                Color(cardViewModel.getCardContent().value.style.secondaryElementsColor())
            else
                MaterialTheme.colors.onSurface



            val textColor = if (cardViewModel.getCardContent().value.fillTextBackground)
                Color(cardViewModel.getCardContent().value.style.textColor())
            else
                MaterialTheme.colors.onSurface

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                    .clickable {
                        cardViewModel.onCardActionClicked(action)
                    }
                    .padding(4.dp)
            ) {
                Text(
                    text = label,
                    color = textColor
                )
            }
        }
    }
}
