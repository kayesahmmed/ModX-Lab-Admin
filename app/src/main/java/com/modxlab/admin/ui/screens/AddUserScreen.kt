package com.modxlab.admin.ui.screens

import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassCard
import com.modxlab.admin.ui.components.GlassCustomValidityDialog
import com.modxlab.admin.ui.components.GlassDropdownMenu
import com.modxlab.admin.ui.components.GlassPrimaryButton
import com.modxlab.admin.ui.components.GlassTextField
import com.modxlab.admin.ui.components.premiumClickable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandEmeraldDark
import com.modxlab.admin.ui.theme.BrandEmeraldLight
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.theme.TextTertiary
import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassCard
import com.modxlab.admin.ui.viewmodel.AdminViewModel

@Composable
fun AddUserScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var validityHours by remember { mutableStateOf(720.0) } // Default 30 Days = 720 Hours
    var validityLabel by remember { mutableStateOf("30 Days") }
    var validityDropdownExpanded by remember { mutableStateOf(false) }
    var showCustomValidityDialog by remember { mutableStateOf(false) }
    var deviceAccess by remember { mutableStateOf("1") } // "1" or "∞"
    var isGenerating by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Bar Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Create Passkey",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Generate and configure user access pass",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Container Card
        GlassBox(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Username Field
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (usernameError != null) usernameError = null
                    },
                    placeholder = { Text("Enter client username", color = TextSecondary.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Username", tint = com.modxlab.admin.ui.theme.BrandSage)
                    },
                    isError = usernameError != null,
                    supportingText = {
                        if (usernameError != null) {
                            Text(usernameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = com.modxlab.admin.ui.theme.AppSurfaceVariant,
                        unfocusedContainerColor = com.modxlab.admin.ui.theme.AppSurfaceVariant,
                        focusedBorderColor = com.modxlab.admin.ui.theme.BrandSage,
                        unfocusedBorderColor = com.modxlab.admin.ui.theme.AppBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_username")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password / Secret Token Field
                Text(
                    text = "Password / Token",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) passwordError = null
                    },
                    placeholder = { Text("Enter client password / token", color = TextSecondary.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password", tint = com.modxlab.admin.ui.theme.BrandSage)
                    },
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = com.modxlab.admin.ui.theme.AppSurfaceVariant,
                        unfocusedContainerColor = com.modxlab.admin.ui.theme.AppSurfaceVariant,
                        focusedBorderColor = com.modxlab.admin.ui.theme.BrandSage,
                        unfocusedBorderColor = com.modxlab.admin.ui.theme.AppBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Clean Validity Period Dropdown Picker
                Text(
                    text = "VALIDITY PERIOD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = com.modxlab.admin.ui.theme.AppBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .premiumClickable { validityDropdownExpanded = true }
                            .testTag("dropdown_validity")
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Validity",
                                        tint = com.modxlab.admin.ui.theme.BrandSage,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = validityLabel,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select",
                                tint = com.modxlab.admin.ui.theme.BrandSage,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    GlassDropdownMenu(
                        expanded = validityDropdownExpanded,
                        onDismissRequest = { validityDropdownExpanded = false },
                        items = listOf(
                            24.0 to "24 Hours (1 Day)",
                            168.0 to "7 Days",
                            360.0 to "15 Days",
                            720.0 to "30 Days",
                            1440.0 to "60 Days",
                            2160.0 to "90 Days",
                            -1.0 to "Custom"
                        ),
                        selectedItem = validityHours,
                        onItemSelected = { hours, label ->
                            if (hours == -1.0) {
                                showCustomValidityDialog = true
                            } else {
                                validityHours = hours
                                validityLabel = label
                            }
                        }
                    )
                }

                if (showCustomValidityDialog) {
                    GlassCustomValidityDialog(
                        onDismissRequest = { showCustomValidityDialog = false },
                        onConfirm = { customHours, label ->
                            validityHours = customHours
                            validityLabel = label
                            showCustomValidityDialog = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))                // Allowed Devices Selector
                Text(
                    text = "DEVICE ACCESS RESTRICTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Single Device Option
                    val isSingle = deviceAccess == "1"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSingle) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .border(width = 1.dp, color = if (isSingle) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppBorder, shape = RoundedCornerShape(10.dp))
                            .clickable { deviceAccess = "1" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1 Device",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isSingle) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Unlimited Option
                    val isUnlimited = deviceAccess == "∞"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isUnlimited) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .border(width = 1.dp, color = if (isUnlimited) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppBorder, shape = RoundedCornerShape(10.dp))
                            .clickable { deviceAccess = "∞" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unlimited",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isUnlimited) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Custom Input Option
                    val isCustom = !isSingle && !isUnlimited
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCustom) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isCustom) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (!isCustom) {
                                    deviceAccess = "2"
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCustom) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = deviceAccess,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isDigit() }) {
                                        deviceAccess = newValue
                                    }
                                },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (deviceAccess.isEmpty()) {
                                            Text(
                                                text = "Custom",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = "Custom",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.modxlab.admin.ui.theme.AppSurfaceVariant,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.addUser(
                                username = username,
                                pass = password,
                                access = deviceAccess,
                                validityHours = validityHours,
                                onSuccess = onNavigateBack
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.modxlab.admin.ui.theme.BrandSage,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Create Key", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
