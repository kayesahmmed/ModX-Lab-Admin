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

@Composable
fun PremiumToast(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = com.modxlab.admin.ui.theme.AppSurface,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    BrandSage,
                    BrandEmerald.copy(alpha = 0.5f),
                    BrandSage.copy(alpha = 0.8f)
                )
            )
        ),
        shadowElevation = 12.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BrandSage.copy(alpha = 0.12f),
                            Color.Transparent,
                            BrandSage.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Neon Glow Left Pill Indicator Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(26.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BrandSage)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Glowing Dual-Ring Badge Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandSage.copy(alpha = 0.18f))
                    .border(1.dp, BrandSage.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (message.contains("ModX", ignoreCase = true)) Icons.Default.AutoAwesome else Icons.Default.CheckCircle,
                    contentDescription = "Notification",
                    tint = BrandSage,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.3.sp
                ),
                modifier = Modifier.weight(1f)
            )

            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .background(BrandSage.copy(alpha = 0.15f))
                        .border(1.dp, BrandSage.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = BrandSage,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
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

