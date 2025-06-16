package pro.progr.lists

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditListDialog(viewModel: ListsViewModel,
                   parentList: NestedList?,
                   nestedList: NestedList?) {
    if (nestedList == null) {
        Log.wtf("nested list is null", "null list")
        return
    }

    val nameState = remember {
        mutableStateOf(nestedList.getName())
    }

    AlertDialog(
        onDismissRequest = { viewModel.editedListIdState.value = null },
        title = { Text(text = "Добавить элемент списка") },
        text = {
            Column {
                if (parentList != null) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Добавляем кружочки
                        repeat(parentList.getNesting().toInt()) {
                            Box(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .size(8.dp)
                                    .background(Color.Gray)
                            )
                        }

                        Text(
                            text = parentList.getName(), modifier = Modifier
                                .padding(start = if (parentList.getNesting() > 0) 8.dp else 0.dp)
                                .fillMaxWidth()
                                .weight(1f)
                        )

                    }

                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
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

                    TextField(
                        modifier = Modifier
                            .padding(start = if (nestedList.getNesting() > 0) 8.dp else 0.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        value = nameState.value,
                        onValueChange = { v: String ->
                            nameState.value = v
                        },
                        placeholder = {Text(text = "Название списка")},
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent
                        )
                    )
                }

            }

        },
        confirmButton = {
            Button(
                onClick = {
                    nestedList.setName(nameState.value)
                    viewModel.updateList(nestedList)
                    viewModel.editedListIdState.value = null
                }
            ) {
                Text("Сохранить")
            }
        }
    )
}