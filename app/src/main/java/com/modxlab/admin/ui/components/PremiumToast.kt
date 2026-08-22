package com.modxlab.admin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandSage
import com.modxlab.admin.ui.theme.TextPrimary
import androidx.compose.animation.animateContentSize

import androidx.compose.ui.draw.shadow

@Composable
fun PremiumToast(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = BrandSage,
                spotColor = BrandEmerald
            )
            .background(com.modxlab.admin.ui.theme.AppSurface, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        BrandSage.copy(alpha = 0.8f),
                        BrandEmerald.copy(alpha = 0.2f),
                        BrandSage.copy(alpha = 0.1f),
                        BrandSage.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Subtle Top-Left Glow
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(BrandEmerald.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )
            
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                BrandSage.copy(alpha = 0.15f),
                                Color.Transparent,
                                BrandSage.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Neon Glow Left Pill Indicator Bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BrandEmerald, BrandSage)
                            )
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Glowing Dual-Ring Badge Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(BrandSage.copy(alpha = 0.2f), BrandEmerald.copy(alpha = 0.05f))
                            )
                        )
                        .border(1.dp, BrandSage.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (message.contains("ModX", ignoreCase = true)) Icons.Default.AutoAwesome else Icons.Default.CheckCircle,
                        contentDescription = "Notification",
                        tint = BrandSage,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                if (actionLabel != null && onActionClick != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .background(BrandSage.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                            .border(1.dp, BrandSage.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .height(36.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = BrandSage,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumSnackbarHost(
    snackbarData: SnackbarData
) {
    PremiumToast(
        message = snackbarData.visuals.message,
        actionLabel = snackbarData.visuals.actionLabel,
        onActionClick = { snackbarData.performAction() }
    )
}

