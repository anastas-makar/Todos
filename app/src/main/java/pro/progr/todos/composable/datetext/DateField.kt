package pro.progr.todos.composable.datetext

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateField(pattern: String, vm : FieldVm) {
    val fields = pattern.split("\\p{Punct}".toRegex()).filter{ !it.isBlank() }
    val separators = pattern.split("[a-zA-Z]+".toRegex()).filter{ !it.isBlank() }
    val formatter = DateTimeFormatter.ofPattern(pattern, java.util.Locale.getDefault())

    val currentValues = arrayOfNulls<String>(fields.size)

    LazyRow(verticalAlignment = Alignment.CenterVertically) {
        itemsIndexed(fields) {index, format ->
            currentValues[index] = vm.dateState.value.format(DateTimeFormatter.ofPattern(format))

            DateItemText(
                currentValues[index]!!
            ) {
                    txtFieldValue ->
                        currentValues[index] = txtFieldValue.text
                val dateString = currentValues.zip(separators).joinToString("") { (a, b) -> "$a$b" } +
                            if (currentValues.size > separators.size) currentValues.last() else ""

                try {
                    vm.dateState.value = LocalDate.parse(dateString, formatter)
                } catch (ex: Exception) {
                    Log.e("exception", "exception", ex)
                }
            }

            if (separators.size > index) {
                Text(text = separators[index], modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}