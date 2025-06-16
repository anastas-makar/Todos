package pro.progr.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ListAdderElement(viewModel: ListsViewModel, nestedList: NestedList) {

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

        Text(
            text = nestedList.getName(),
            modifier = Modifier
                .padding(start = if (nestedList.getNesting() > 0) 8.dp else 0.dp)
                .fillMaxWidth()
                .weight(1f)
        )

        IconButton(
            onClick = {
                viewModel.addSublistToList(nestedList)
            },
            modifier = Modifier
                .width(50.dp)
                .weight(0.1f)
        ) {

            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Добавить в список",
                modifier = Modifier
            )

        }
    }

}