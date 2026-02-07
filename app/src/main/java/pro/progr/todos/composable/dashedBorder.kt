package pro.progr.todos.composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 6.dp,
    cap: StrokeCap = StrokeCap.Round,
    phasePx: Float = 0f,
): Modifier = drawBehind {
    val strokeWidth = width.toPx()
    val half = strokeWidth / 2f

    // Чтобы штрих не резало по границе — рисуем по inset-области
    val outline = shape.createOutline(
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        layoutDirection = layoutDirection,
        density = this
    )

    val effect = PathEffect.dashPathEffect(
        floatArrayOf(dashLength.toPx(), gapLength.toPx()),
        phasePx
    )

    // Сдвигаем на half stroke внутрь
    translate(half, half) {
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(
                width = strokeWidth,
                pathEffect = effect,
                cap = cap
            )
        )
    }
}
