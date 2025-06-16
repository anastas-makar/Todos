package pro.progr.brightcards.composable.description

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun transparentAnnotatedString() : AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colors.onSurface)) {
            append("Тег1")
            addStringAnnotation(
                tag = "tag",
                annotation = "Тег1",
                start = length - "Тег1".length,
                end = length
            )
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colors.onSurface)) {
            append(" здесь текст и ")
        }
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colors.onSurface)) {
            append("ссылка на изображение")
            addStringAnnotation(
                tag = "image",
                annotation = "https://url.to.image.com",
                start = length - "ссылка на изображение".length,
                end = length
            )
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colors.onSurface)) {
            append(". И еще текст.")
        }
    }
}