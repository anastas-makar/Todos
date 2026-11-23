package pro.progr.todos.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import pro.progr.todos.R

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_icon))
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.clear_search))
                }
            }
        } else null,
        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White
        )
    )
}
