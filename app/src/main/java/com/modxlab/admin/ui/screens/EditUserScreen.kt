package com.modxlab.admin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.modxlab.admin.data.model.UserEntity
import com.modxlab.admin.ui.theme.BrandCrimson
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandEmeraldLight
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.StatusActive
import com.modxlab.admin.ui.theme.StatusInactive
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.theme.TextTertiary
import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassCard
import com.modxlab.admin.ui.viewmodel.AdminViewModel

@Composable
fun EditUserScreen(
    userKey: String,
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var userEntity by remember { mutableStateOf<UserEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var accessLimit by remember { mutableStateOf("1") }
    var customAccessText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showResetHwidDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userKey) {
        val u = viewModel.getUser(userKey)
        if (u != null) {
            userEntity = u
            username = u.user
            password = u.pass
            val acc = u.access
            if (acc == "1" || acc == "0" || acc == "unlimited") {
                accessLimit = if (acc == "0" || acc == "unlimited") "unlimited" else "1"
            } else {
                accessLimit = "custom"
                customAccessText = acc
            }
        }
        isLoading = false
    }

    val scrollState = rememberScrollState()

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandEmerald)
        }
        return
    }

    if (userEntity == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("User not found", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val user = userEntity!!
    val isDeviceBound = user.device != "null" && user.device.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.modxlab.admin.ui.theme.AppSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Edit Key Settings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Manage credentials & status",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandCrimson.copy(alpha = 0.15f))
                    .testTag("btn_delete_user_screen")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete User",
                    tint = BrandCrimson
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // License Key Display Banner
        GlassBox(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Key",
                            tint = com.modxlab.admin.ui.theme.BrandSage,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LICENSE KEY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = user.key,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = com.modxlab.admin.ui.theme.BrandSage
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ModX License Key", user.key)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToastMessage("Key copied to clipboard")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Credentials Form Card
        GlassBox(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Account Credentials",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

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
                    onValueChange = { username = it },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Username", tint = com.modxlab.admin.ui.theme.BrandSage)
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
                        .testTag("edit_input_username")
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    onValueChange = { password = it },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password", tint = com.modxlab.admin.ui.theme.BrandSage)
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
                        .testTag("edit_input_password")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Device Access Limit in one line
                Text(
                    text = "Device Access Limit",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isOne = accessLimit == "1"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isOne) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isOne) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { accessLimit = "1" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1 Device",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isOne) Color.White else TextPrimary
                            ),
                            maxLines = 1
                        )
                    }

                    val isUnlimited = accessLimit == "unlimited"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isUnlimited) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isUnlimited) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { accessLimit = "unlimited" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unlimited",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlimited) Color.White else TextPrimary
                            ),
                            maxLines = 1
                        )
                    }

                    val isCustom = accessLimit == "custom"
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
                            .clickable { accessLimit = "custom" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCustom) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = customAccessText,
                                onValueChange = { customAccessText = it.filter { ch -> ch.isDigit() } },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (customAccessText.isEmpty()) {
                                            Text(
                                                text = "Custom",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = Color.White.copy(alpha = 0.5f),
                                                    fontWeight = FontWeight.SemiBold,
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
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isSaving = true
                        val finalAccess = when (accessLimit) {
                            "unlimited" -> "unlimited"
                            "custom" -> if (customAccessText.isNotBlank()) customAccessText else "1"
                            else -> "1"
                        }
                        viewModel.updateUserFull(
                            key = user.key,
                            user = username,
                            pass = password,
                            access = finalAccess,
                            status = user.status,
                            resetDevice = false,
                            onSuccess = {
                                isSaving = false
                                userEntity = userEntity?.copy(user = username, pass = password, access = finalAccess)
                            }
                        )
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = com.modxlab.admin.ui.theme.BrandSage, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_save_user_changes")
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activation Toggle Card (Block / Unblock) - Premium Styled
        val activeColor = com.modxlab.admin.ui.theme.BrandSage
        val inactiveColor = BrandCrimson
        val currentStatusColor = if (user.isActive) activeColor else inactiveColor

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = com.modxlab.admin.ui.theme.AppSurface,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                color = currentStatusColor.copy(alpha = 0.45f)
            ),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Glowing Dual Ring Icon Container
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(currentStatusColor.copy(alpha = 0.12f))
                                .border(1.dp, currentStatusColor.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "Status",
                                tint = currentStatusColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (user.isActive) "License Active" else "User Blocked",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Status Pill Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(currentStatusColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (user.isActive) "ACTIVE" else "BLOCKED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = currentStatusColor,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (user.isActive) "Client can login & authenticate" else "Client key is blocked from login",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = user.isActive,
                        onCheckedChange = { willActivate ->
                            viewModel.toggleUserStatus(user.key, willActivate)
                            userEntity = userEntity?.copy(status = if (willActivate) "true" else "false")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = activeColor,
                            checkedTrackColor = activeColor.copy(alpha = 0.3f),
                            uncheckedThumbColor = inactiveColor,
                            uncheckedTrackColor = inactiveColor.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("switch_user_status")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware & Session Telemetry Card with Reset HWID
        GlassBox(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hardware & Telemetry Info",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    if (isDeviceBound) {
                        Button(
                            onClick = { showResetHwidDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.15f),
                                contentColor = com.modxlab.admin.ui.theme.BrandSage
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Reset HWID", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                InfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Registration Timestamp",
                    value = user.rgtime.ifEmpty { "N/A" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "License Expiry Date",
                    value = user.validity.ifEmpty { "30 Days from registration" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.Devices,
                    label = "Hardware Access Restriction",
                    value = if (user.isUnlimitedDevice) "Unlimited Devices" else "${user.access} Device(s) Allowed"
                )
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }

    if (showResetHwidDialog) {
        AlertDialog(
            onDismissRequest = { showResetHwidDialog = false },
            title = { Text("Reset Bound Device", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)) },
            text = {
                Text(
                    "Reset bound device for \"${user.user}\"? This clears the hardware lock and allows login from a new device.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetUserHwid(user.key)
                        userEntity = userEntity?.copy(device = "null")
                        showResetHwidDialog = false
                    }
                ) {
                    Text("Reset Device", color = com.modxlab.admin.ui.theme.BrandSage, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetHwidDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = com.modxlab.admin.ui.theme.AppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete License User", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)) },
            text = {
                Text(
                    "Are you sure you want to permanently delete user \"${user.user}\"? All credentials and device bindings will be purged.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(user.key)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = BrandCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = com.modxlab.admin.ui.theme.AppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(com.modxlab.admin.ui.theme.AppSurfaceVariant)
            .border(
                width = 0.8.dp,
                color = com.modxlab.admin.ui.theme.AppBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = com.modxlab.admin.ui.theme.BrandSage,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )
        }
    }
}
