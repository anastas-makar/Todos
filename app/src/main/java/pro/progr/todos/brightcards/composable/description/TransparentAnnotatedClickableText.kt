package pro.progr.brightcards.composable.description

import android.util.Log
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.TransparentAnnotatedClickableText(annotatedString: AnnotatedString) {

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(start = offset, end = offset).firstOrNull()?.let { annotation ->
                when (annotation.tag) {
                    "tag" -> {
                        // обработка клика по тегу
                        Log.d("click", "Клик по тегу: ${annotation.item}")
                    }
                    "image" -> {
                        // обработка клика по ссылке на изображение
                        Log.d("click","Клик по изображению: ${annotation.item}")
                    }
                }
            }
        },
        style = MaterialTheme.typography.body1,
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.Start)
    )
}