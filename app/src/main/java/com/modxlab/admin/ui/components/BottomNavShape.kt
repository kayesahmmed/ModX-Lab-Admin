package com.modxlab.admin.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

class BottomNavShape(
    private val cradleRadius: Dp,
    private val cornerRadius: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        val crPx = with(density) { cradleRadius.toPx() }
        val cornerPx = with(density) { cornerRadius.toPx() }
        
        val path = Path().apply {
            // Start at top-left, after the corner
            moveTo(0f, cornerPx)
            // Top-left corner
            arcTo(
                rect = Rect(0f, 0f, cornerPx * 2, cornerPx * 2),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            
            // Draw line to the start of the cutout
            val center = size.width / 2f
            val cutoutStart = center - crPx - 10f // some padding
            val cutoutEnd = center + crPx + 10f
            
            lineTo(cutoutStart, 0f)
            
            // Draw the cradle arc (a smooth dip)
            // We can approximate a curve or just draw a semicircular dip
            cubicTo(
                cutoutStart + crPx / 2f, 0f,
                center - crPx, crPx,
                center, crPx
            )
            cubicTo(
                center + crPx, crPx,
                cutoutEnd - crPx / 2f, 0f,
                cutoutEnd, 0f
            )
            
            // Line to top-right corner
            lineTo(size.width - cornerPx, 0f)
            // Top-right corner
            arcTo(
                rect = Rect(size.width - cornerPx * 2, 0f, size.width, cornerPx * 2),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            
            // Right edge
            lineTo(size.width, size.height)
            // Bottom edge
            lineTo(0f, size.height)
            // Left edge
            close()
        }
        
        return Outline.Generic(path)
    }
}
