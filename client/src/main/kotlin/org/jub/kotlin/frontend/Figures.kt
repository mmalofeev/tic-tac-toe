package org.jub.kotlin.frontend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ticTacToeGrid() {
    Canvas(
        modifier = Modifier
            .size(600.dp)
            .padding(10.dp),
    ) {
        drawGridLine(size.width / 3, 0f, size.width / 3, size.height)
        drawGridLine(size.width * 2 / 3, 0f, size.width * 2 / 3, size.height)
        drawGridLine(0f, size.height / 3, size.width, size.height / 3)
        drawGridLine(0f, size.height * 2 / 3, size.width, size.height * 2 / 3)
    }
}

fun DrawScope.drawGridLine(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float
) {
    drawLine(
        color = Color.Gray,
        strokeWidth = 5f,
        start = Offset(x = startX, y = startY),
        end = Offset(x = endX, y = endY)
    )
}

@Composable
fun nought() {
    Canvas(
        modifier = Modifier
            .size(150.dp)
    ) {
        drawCircle(
            color = Color.Cyan,
            style = Stroke(width = 20f)
        )
    }
}

@Composable
fun cross() {
    Canvas(
        modifier = Modifier
            .size(150.dp)
    ) {
        drawLine(
            color = Color.Yellow,
            strokeWidth = 20f,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = size.width, y = size.height)
        )
        drawLine(
            color = Color.Yellow,
            strokeWidth = 20f,
            start = Offset(x = 0f, y = size.height),
            end = Offset(x = size.width, y = 0f)
        )
    }
}
