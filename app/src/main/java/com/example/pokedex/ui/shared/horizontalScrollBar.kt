package com.example.pokedex.ui.shared

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.horizontalScrollBar(
    scrollState: ScrollState,
    height: Dp = 4.dp,
    showScrollBarTrack: Boolean = true,
    scrollBarTrackColor: Color = Color.Gray,
    scrollBarColor: Color = Color.Black,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f
): Modifier {
    return drawWithContent {
        drawContent()

        val viewportWidth = this.size.width
        val totalContentWidth = scrollState.maxValue.toFloat() + viewportWidth

        if (totalContentWidth > viewportWidth) {
            val scrollValue = scrollState.value.toFloat()
            val scrollBarWidth = (viewportWidth / totalContentWidth) * viewportWidth
            val scrollBarStartOffset = (scrollValue / totalContentWidth) * viewportWidth

            if (showScrollBarTrack) {
                drawRoundRect(
                    cornerRadius = CornerRadius(scrollBarCornerRadius),
                    color = scrollBarTrackColor,
                    topLeft = Offset(0f, this.size.height - endPadding),
                    size = Size(viewportWidth, height.toPx()),
                )
            }

            drawRoundRect(
                cornerRadius = CornerRadius(scrollBarCornerRadius),
                color = scrollBarColor,
                topLeft = Offset(scrollBarStartOffset, this.size.height - endPadding),
                size = Size(scrollBarWidth, height.toPx())
            )
        }
    }
}
