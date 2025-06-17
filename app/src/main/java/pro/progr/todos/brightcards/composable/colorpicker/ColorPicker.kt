package pro.progr.todos.brightcards.composable.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pro.progr.todos.brightcards.carousel.CarouselPager
import pro.progr.todos.brightcards.colors.ColorsProvider
import pro.progr.todos.brightcards.composable.ColorItem
import pro.progr.todos.brightcards.vm.CardViewModel
import pro.progr.todos.brightcards.vm.PaletteViewModel

@Composable
fun ColorPicker(cardViewModel: CardViewModel, paletteViewModel: PaletteViewModel) {

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 20.dp/*, start = 25.dp, end = 25.dp*/)) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            TextButton(
                onClick = {
                    cardViewModel.updateCard(cardViewModel.getCardContent().value.copy(fillTitleBackground = !cardViewModel.getCardContent().value.fillTitleBackground))
                },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Row {
                    Text(text = "ЗАГОЛОВОК", color = MaterialTheme.colors.onSurface)

                    Spacer(modifier = Modifier.width(5.dp))

                    Box(modifier = Modifier
                        .background(
                            color = if (cardViewModel.getCardContent().value.fillTitleBackground)
                                Color(cardViewModel.getCardContent().value.style.backgroundColor())
                            else
                                Color.Transparent,
                            shape = CircleShape
                        )
                        .width(20.dp)
                        .height(20.dp))

                }
            }

            //Spacer(modifier = Modifier.width(5.dp))

            TextButton(onClick = {
                cardViewModel.updateCard(cardViewModel.getCardContent().value.copy(fillTextBackground = !cardViewModel.getCardContent().value.fillTextBackground))
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row {
                    Box(modifier = Modifier
                        .background(
                            color = if (cardViewModel.getCardContent().value.fillTextBackground)
                                Color(cardViewModel.getCardContent().value.style.backgroundColor())
                            else
                                Color.Transparent,
                            shape = CircleShape
                        )
                        .width(20.dp)
                        .height(20.dp))



                    Spacer(modifier = Modifier.width(5.dp))

                    Text(text = "ОПИСАНИЕ", color = MaterialTheme.colors.onSurface)

                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            itemsIndexed(ColorsProvider.getPalette(paletteViewModel.currentPage.value.paletteType).chunked(4)) { _, rowColors ->  // создание сетки из 4 цветов в ряд
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                    .padding(8.dp)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)) {
                    rowColors.forEach { color ->
                        Box(modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 50.dp, max = 100.dp),
                        contentAlignment = Alignment.Center) {
                            ColorItem(color = color, selectedColor = cardViewModel.getCardContent().value.style, onColorSelected = { color ->
                                cardViewModel.updateCard(cardViewModel.getCardContent().value.copy(style = color))
                            })
                        }
                    }
                }
            }
        }

        Row {
            CarouselPager(paletteViewModel)
            val checkedPaletteType = remember {paletteViewModel.defaultPalette}
            Checkbox(
                checked = paletteViewModel.currentPage.value.paletteType == checkedPaletteType.value.paletteType,
                onCheckedChange = { checked ->
                    if (checked) {
                        paletteViewModel.updateDefaultPalette(paletteViewModel.currentPage.value)
                    }
                })
            Text(text = "Выбрать", modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}
