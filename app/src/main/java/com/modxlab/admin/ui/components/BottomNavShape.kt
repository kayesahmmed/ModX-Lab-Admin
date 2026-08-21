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
    private val cradleRadius: Dp = 28.dp,
    private val shoulderRadius: Dp = 12.dp,
    private val cradleDepth: Dp = 32.dp,
    private val cornerRadius: Dp = 16.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        val rPx = with(density) { cradleRadius.toPx() }
        val sPx = with(density) { shoulderRadius.toPx() }
        val depthPx = with(density) { cradleDepth.toPx() }
        val cornerPx = with(density) { cornerRadius.toPx() }

        val center = size.width / 2f
        val x0 = center - rPx - sPx
        val x1 = center - rPx
        val x3 = center + rPx
        val x4 = center + rPx + sPx
        val yShoulder = depthPx * 0.25f

        val path = Path().apply {
            // Start at top-left corner
            moveTo(0f, cornerPx)
            arcTo(
                rect = Rect(0f, 0f, cornerPx * 2, cornerPx * 2),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Flat top edge to cutout shoulder start
            lineTo(x0, 0f)

            // Left shoulder curve
            cubicTo(
                x0 + sPx * 0.5f, 0f,
                x1 - sPx * 0.2f, yShoulder,
                x1, yShoulder
            )

            // Cradle left curve
            cubicTo(
                x1 + rPx * 0.35f, yShoulder + (depthPx - yShoulder) * 0.75f,
                center - rPx * 0.35f, depthPx,
                center, depthPx
            )

            // Cradle right curve
            cubicTo(
                center + rPx * 0.35f, depthPx,
                x3 - rPx * 0.35f, yShoulder + (depthPx - yShoulder) * 0.75f,
                x3, yShoulder
            )

            // Right shoulder curve
            cubicTo(
                x3 + sPx * 0.2f, yShoulder,
                x4 - sPx * 0.5f, 0f,
                x4, 0f
            )

            // Flat top edge to top-right corner
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




