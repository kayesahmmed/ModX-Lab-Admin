package com.modxlab.admin.ui.components

import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.modxlab.admin.R
import com.modxlab.admin.ui.theme.BrandCrimson
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald

// Reusable Premium Bouncy Scale Click Modifier
fun Modifier.premiumClickable(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (onClick == null) return@composed this

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "premiumClickScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

// Modern Frosted Glass Color Tokens (50% Opacity)
val GlassBackgroundTop = Color.Black.copy(alpha = 0.50f)
val GlassBackgroundBottom = Color.Black.copy(alpha = 0.50f)
val GlassTint = Color.Black.copy(alpha = 0.50f)

val GlassBorderTop = Color.White.copy(alpha = 0.35f)
val GlassBorderBottom = Color.White.copy(alpha = 0.12f)

val GlassBorderBrush = Brush.verticalGradient(
    colors = listOf(GlassBorderTop, GlassBorderBottom)
)

val GlassBackgroundBrush = Brush.verticalGradient(
    colors = listOf(GlassBackgroundTop, GlassBackgroundBottom)
)

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var positionInRoot by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, spotColor = Color.Black.copy(alpha = 0.25f))
            .clip(shape)
            .onGloballyPositioned { coordinates ->
                positionInRoot = coordinates.positionInRoot()
            }
    ) {
        // Position-aligned blurred background image window (50% opacity)
        Image(
            painter = painterResource(id = R.drawable.nature_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .blur(radius = 32.dp)
                .alpha(0.50f)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints.fixed(screenWidthPx.toInt(), screenHeightPx.toInt())
                    )
                    layout(constraints.minWidth, constraints.minHeight) {
                        placeable.placeRelative(
                            x = -positionInRoot.x.toInt(),
                            y = -positionInRoot.y.toInt()
                        )
                    }
                }
        )

        // Frosted glass 50% opacity background tint overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.50f))
                .border(
                    width = 0.8.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.12f))
                    ),
                    shape = shape
                )
        )

        // Box Content
        Box(content = content)
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var positionInRoot by remember { mutableStateOf(Offset.Zero) }

    val clickModifier = if (onClick != null) {
        Modifier.premiumClickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, spotColor = Color.Black.copy(alpha = 0.25f))
            .clip(shape)
            .onGloballyPositioned { coordinates ->
                positionInRoot = coordinates.positionInRoot()
            }
            .then(clickModifier)
    ) {
        // Position-aligned blurred background image window (50% opacity)
        Image(
            painter = painterResource(id = R.drawable.nature_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .blur(radius = 32.dp)
                .alpha(0.50f)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints.fixed(screenWidthPx.toInt(), screenHeightPx.toInt())
                    )
                    layout(constraints.minWidth, constraints.minHeight) {
                        placeable.placeRelative(
                            x = -positionInRoot.x.toInt(),
                            y = -positionInRoot.y.toInt()
                        )
                    }
                }
        )

        // Frosted glass 50% opacity background tint overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.50f))
                .border(
                    width = 0.8.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.12f))
                    ),
                    shape = shape
                )
        )

        // Card Content
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// Premium Custom Glowing Checkbox Component
@Composable
fun GlassCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activeColor: Color = BrandEmerald
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkboxScale"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (checked) activeColor else Color.White.copy(alpha = 0.15f)
            )
            .border(
                width = 1.5.dp,
                color = if (checked) activeColor else Color.White.copy(alpha = 0.40f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(enabled = enabled && onCheckedChange != null) {
                onCheckedChange?.invoke(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Premium Glowing Status Badge Chip
@Composable
fun StatusBadge(
    text: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = BrandEmerald,
    inactiveColor: Color = BrandCrimson
) {
    val badgeColor = if (isActive) activeColor else inactiveColor

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeColor.copy(alpha = 0.22f))
            .border(
                width = 0.8.dp,
                color = badgeColor.copy(alpha = 0.50f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(badgeColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = badgeColor,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// Ultra-Premium Glass Dropdown Menu
@Composable
fun <T> GlassDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<Pair<T, String>>,
    selectedItem: T,
    onItemSelected: (T, String) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF101424).copy(alpha = 0.96f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(BrandEmerald.copy(alpha = 0.60f), Color.White.copy(alpha = 0.18f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 6.dp, horizontal = 6.dp)
    ) {
        items.forEach { (value, label) ->
            val isSelected = value == selectedItem
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = BrandEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                onClick = {
                    onItemSelected(value, label)
                    onDismissRequest()
                },
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) BrandEmerald.copy(alpha = 0.22f) else Color.Transparent
                    )
            )
        }
    }
}

// Ultra-Premium Glass Input Field
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    testTag: String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 0.8.sp
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.40f)) },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BrandEmerald.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(leadingIcon, contentDescription = label, tint = BrandEmerald, modifier = Modifier.size(18.dp))
                }
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Black.copy(alpha = 0.50f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.50f),
                focusedBorderColor = BrandEmerald,
                unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

// Ultra-Premium Primary Action Button
@Composable
fun GlassPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = BrandEmerald.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(BrandEmerald, BrandCyan)
                )
            )
            .premiumClickable(enabled = !isLoading, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.Black,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

// Ultra-Premium Custom Validity Dialog
@Composable
fun GlassCustomValidityDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (validityHours: Double, label: String) -> Unit
) {
    var unitMode by remember { mutableStateOf("HOURS") } // "HOURS" or "DAYS"
    var inputValue by remember { mutableStateOf("12") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val numericVal = inputValue.toDoubleOrNull() ?: 0.0
    val totalHours = if (unitMode == "HOURS") numericVal else numericVal * 24.0

    val calculatedLabel = remember(unitMode, inputValue) {
        val num = inputValue.toDoubleOrNull()
        if (num == null || num <= 0) "Invalid Duration"
        else if (unitMode == "HOURS") {
            if (num == 1.0) "1 Hour" else "${num.toInt()} Hours"
        } else {
            if (num == 1.0) "1 Day (24 Hours)" else "${num.toInt()} Days"
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandEmerald.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = "Custom Validity",
                                tint = BrandEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CUSTOM VALIDITY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .premiumClickable { onDismissRequest() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Unit Mode Segmented Selector (HOURS vs DAYS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.50f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isHours = unitMode == "HOURS"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isHours) BrandEmerald else Color.Transparent
                            )
                            .premiumClickable { unitMode = "HOURS" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏳ HOURS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isHours) Color.Black else Color.White.copy(alpha = 0.80f)
                            )
                        )
                    }

                    val isDays = unitMode == "DAYS"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isDays) BrandEmerald else Color.Transparent
                            )
                            .premiumClickable { unitMode = "DAYS" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📅 DAYS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDays) Color.Black else Color.White.copy(alpha = 0.80f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Numeric Input Field
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = {
                        inputValue = it.filter { char -> char.isDigit() }
                        if (errorText != null) errorText = null
                    },
                    label = { Text("ENTER ${unitMode.uppercase()} AMOUNT", color = Color.White.copy(alpha = 0.7f)) },
                    placeholder = { Text(if (unitMode == "HOURS") "e.g. 12" else "e.g. 45", color = Color.White.copy(alpha = 0.35f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorText != null,
                    supportingText = {
                        if (errorText != null) {
                            Text(errorText!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text(
                                text = "Preview: $calculatedLabel",
                                color = BrandEmerald,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Black.copy(alpha = 0.50f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.50f),
                        focusedBorderColor = BrandEmerald,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons Row with Identical Heights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CANCEL Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(14.dp))
                            .premiumClickable { onDismissRequest() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CANCEL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.90f),
                                fontSize = 14.sp
                            )
                        )
                    }

                    // APPLY Button
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = BrandEmerald.copy(alpha = 0.5f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(BrandEmerald, BrandCyan)
                                )
                            )
                            .premiumClickable {
                                val num = inputValue.toDoubleOrNull()
                                if (num == null || num <= 0) {
                                    errorText = "Please enter a valid number (> 0)"
                                } else {
                                    onConfirm(totalHours, calculatedLabel)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "APPLY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
