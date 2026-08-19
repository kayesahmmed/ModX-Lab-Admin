package com.modxlab.admin.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
    var validityDays by remember { mutableStateOf(30) }
    var validityLabel by remember { mutableStateOf("30 Days") }
    var validityDropdownExpanded by remember { mutableStateOf(false) }
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
                        color = Color.White
                    )
                )
                Text(
                    text = "Generate and configure user access pass",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Container Card
        GlassBox(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Username Field
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (usernameError != null) usernameError = null
                    },
                    placeholder = { Text("Enter client username", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Username", tint = BrandEmeraldLight)
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
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandEmerald,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
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
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) passwordError = null
                    },
                    placeholder = { Text("Enter client password / token", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password", tint = BrandEmeraldLight)
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
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandEmerald,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Validity Period Dropdown Picker
                Text(
                    text = "Validity Period",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { validityDropdownExpanded = true }
                            .testTag("dropdown_validity")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Validity",
                                    tint = BrandEmeraldLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = validityLabel,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select",
                                tint = TextSecondary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = validityDropdownExpanded,
                        onDismissRequest = { validityDropdownExpanded = false },
                        modifier = Modifier.background(CyberSurfaceVariant)
                    ) {
                        listOf(
                            1 to "24 Hours (1 Day)",
                            7 to "7 Days",
                            15 to "15 Days",
                            30 to "30 Days",
                            60 to "60 Days",
                            90 to "90 Days"
                        ).forEach { (days, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = Color.White) },
                                onClick = {
                                    validityDays = days
                                    validityLabel = label
                                    validityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Allowed Devices Selector
                Text(
                    text = "Device Access Restriction",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Single Device Option
                    val isSingle = deviceAccess == "1"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSingle) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSingle) BrandEmerald else Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { deviceAccess = "1" }
                            .testTag("radio_single_device")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSingle,
                                onClick = { deviceAccess = "1" },
                                colors = RadioButtonDefaults.colors(selectedColor = BrandEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "1 Device",
                                    maxLines = 1,
                                    softWrap = false,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "HWID locked",
                                    maxLines = 1,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Unlimited Device Option
                    val isUnlimited = deviceAccess == "∞"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isUnlimited) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isUnlimited) BrandEmerald else Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { deviceAccess = "∞" }
                            .testTag("radio_unlimited_device")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isUnlimited,
                                onClick = { deviceAccess = "∞" },
                                colors = RadioButtonDefaults.colors(selectedColor = BrandEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Unlimited",
                                    maxLines = 1,
                                    softWrap = false,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "Multi-device",
                                    maxLines = 1,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                var hasError = false
                if (username.isBlank()) {
                    usernameError = "User cannot be blank!"
                    hasError = true
                }
                if (password.isBlank()) {
                    passwordError = "Enter a password / token!"
                    hasError = true
                }
                if (!hasError) {
                    isGenerating = true
                    viewModel.addUser(
                        username = username,
                        pass = password,
                        access = deviceAccess,
                        validityDays = validityDays,
                        onSuccess = {
                            isGenerating = false
                            onNavigateBack()
                        }
                    )
                }
            },
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandEmerald,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_generate_user_key")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Key Generating...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Generate",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GENERATE & ACTIVATE KEY",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}
