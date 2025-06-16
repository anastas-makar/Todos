package pro.progr.todos.composable.datetext

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun DateItemText(text: String, onValueChange: (TextFieldValue) -> Unit) {
    val txtValue = remember { mutableStateOf(TextFieldValue(text)) }

    BasicTextField(
        value = txtValue.value,
        onValueChange = { textValue: TextFieldValue ->
            txtValue.value = textValue
            onValueChange(textValue)
        },
        modifier = Modifier
            .width((12 * text.length).dp)
            .padding(horizontal = 0.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = true,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    )
}