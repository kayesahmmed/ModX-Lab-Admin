package com.modxlab.admin.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class BottomNavShape(
    private val cradleRadius: Dp = 24.dp,
    private val cradleMargin: Dp = 6.dp,
    private val cornerRadius: Dp = 14.dp,
    private val cradleDepth: Dp = 24.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        val crPx = with(density) { cradleRadius.toPx() }
        val marginPx = with(density) { cradleMargin.toPx() }
        val cornerPx = with(density) { cornerRadius.toPx() }
        val depthPx = with(density) { cradleDepth.toPx() }
        
        val center = size.width / 2f
        val totalCutoutRadius = crPx + marginPx
        val shoulder = marginPx * 1.2f

        val path = Path().apply {
            // Start at top-left corner
            moveTo(0f, cornerPx)
            arcTo(
                rect = Rect(0f, 0f, cornerPx * 2, cornerPx * 2),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Line towards the center cutout start
            val cutoutStart = center - totalCutoutRadius - shoulder
            val cutoutEnd = center + totalCutoutRadius + shoulder
            lineTo(cutoutStart, 0f)

            // Smooth gentle entry shoulder into cradle
            cubicTo(
                cutoutStart + shoulder * 0.7f, 0f,
                center - totalCutoutRadius, depthPx * 0.4f,
                center - totalCutoutRadius * 0.7f, depthPx * 0.85f
            )
            // Cradle base curve
            cubicTo(
                center - totalCutoutRadius * 0.35f, depthPx,
                center + totalCutoutRadius * 0.35f, depthPx,
                center + totalCutoutRadius * 0.7f, depthPx * 0.85f
            )
            // Smooth gentle exit shoulder out of cradle
            cubicTo(
                center + totalCutoutRadius, depthPx * 0.4f,
                cutoutEnd - shoulder * 0.7f, 0f,
                cutoutEnd, 0f
            )

            // Line to top-right corner
            lineTo(size.width - cornerPx, 0f)
            arcTo(
                rect = Rect(size.width - cornerPx * 2, 0f, size.width, cornerPx * 2),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right, bottom, left edges
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        return Outline.Generic(path)
    }
}

