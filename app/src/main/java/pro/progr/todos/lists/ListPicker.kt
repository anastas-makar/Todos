package pro.progr.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ListPicker(listsViewModel: ListsViewModel) {

    LazyColumn (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 0.dp, start = 20.dp, end = 20.dp)) {


            items(listsViewModel.nestedListState.value.size) { index ->
                val nestedList = listsViewModel.nestedListState.value[index]
                // Обводим рамкой, если элемент равен selectedListState
                Box(
                    modifier = (if (nestedList == listsViewModel.selectedListState.value)
                                    Modifier.border(1.dp, Color.Gray)
                                else
                                    Modifier)
                        .clickable {
                            listsViewModel.selectedListState.value = nestedList
                            listsViewModel.sheetMode.value = ListsViewModel.SheetMode.HIDE
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Добавляем кружочки
                        repeat(nestedList.getNesting().toInt()) {
                            Box(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .size(8.dp)
                                    .background(Color.Gray)
                            )
                        }

                        // Добавляем имя
                        Text(
                            text = nestedList.getName(),
                            modifier = Modifier.padding(start = if (nestedList.getNesting() > 0) 8.dp else 0.dp)
                        )
                    }
                }
            }


    }
}
