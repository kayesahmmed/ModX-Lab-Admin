package com.modxlab.admin.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedFillIcon(
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier
) {
    val fillProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "fill_animation"
    )

    Box(modifier = modifier) {
        // Base unselected icon
        Icon(
            imageVector = unselectedIcon,
            contentDescription = null,
            tint = unselectedColor,
            modifier = Modifier.matchParentSize()
        )

        // Filled icon clipped from bottom to top
        Icon(
            imageVector = selectedIcon,
            contentDescription = null,
            tint = selectedColor,
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    // height of the portion that is visible (from bottom)
                    val visibleHeight = size.height * fillProgress
                    val topEdge = size.height - visibleHeight

                    clipRect(
                        left = 0f,
                        top = topEdge,
                        right = size.width,
                        bottom = size.height
                    ) {
                        this@drawWithContent.drawContent()
                    }
                }
        )
    }
}
