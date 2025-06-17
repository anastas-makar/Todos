package pro.progr.todos.brightcards.carousel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import pro.progr.todos.R
import pro.progr.todos.brightcards.vm.PaletteViewModel

@Composable
fun CarouselPager(paletteViewModel : PaletteViewModel) {

    Row {

        IconButton(
            onClick = { paletteViewModel.prevPage() }
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
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
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.right)
            )
        }

    }
}