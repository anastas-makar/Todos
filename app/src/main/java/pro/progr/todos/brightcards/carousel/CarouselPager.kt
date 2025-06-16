package pro.progr.brightcards.carousel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import pro.progr.todos.R
import pro.progr.brightcards.vm.PaletteViewModel

@Composable
fun CarouselPager(paletteViewModel : PaletteViewModel) {

    Row {

        IconButton(
            onClick = { paletteViewModel.prevPage() }
        ) {
            Icon(
                Icons.Outlined.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.left)
            )
        }

        Text(
            modifier = Modifier.wrapContentWidth().align(Alignment.CenterVertically),
            text = paletteViewModel.currentPage.value.getPageName(),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { paletteViewModel.nextPage() }
        ) {
            Icon(
                Icons.Outlined.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.right)
            )
        }

    }
}