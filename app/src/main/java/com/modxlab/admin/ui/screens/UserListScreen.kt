package com.modxlab.admin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassCard
import com.modxlab.admin.ui.components.GlassCheckbox
import com.modxlab.admin.ui.components.StatusBadge
import com.modxlab.admin.ui.components.premiumClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassCard
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.StatusActive
import com.modxlab.admin.ui.theme.StatusActiveBg
import com.modxlab.admin.ui.theme.StatusInactive
import com.modxlab.admin.ui.theme.StatusInactiveBg
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.theme.TextTertiary
import com.modxlab.admin.ui.viewmodel.AdminViewModel

@Composable
fun UserListScreen(
    viewModel: AdminViewModel,
    onNavigateToAddUser: () -> Unit,
    onNavigateToEditUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val users by viewModel.filteredUsers.collectAsState()
    val searchQuery by viewModel.userSearchQuery.collectAsState()
    val statusFilter by viewModel.userStatusFilter.collectAsState()

    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    var userToToggle by remember { mutableStateOf<UserEntity?>(null) }
    var userToResetHwid by remember { mutableStateOf<UserEntity?>(null) }

    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedKeys by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = onNavigateToAddUser,
                    containerColor = BrandEmerald,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_add_user")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add User")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Title / Selection Action Bar
            if (isSelectionMode) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = com.modxlab.admin.ui.theme.AppSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.8f)
                    ),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isSelectionMode = false
                                    selectedKeys = emptySet()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Cancel Selection", tint = TextPrimary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${selectedKeys.size} / ${users.size} Selected",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = {
                                    if (selectedKeys.size == users.size) {
                                        selectedKeys = emptySet()
                                    } else {
                                        selectedKeys = users.map { it.key }.toSet()
                                    }
                                }
                            ) {
                                Text(
                                    text = if (selectedKeys.size == users.size) "Deselect All" else "Select All",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = com.modxlab.admin.ui.theme.BrandSage,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (selectedKeys.isNotEmpty()) {
                                        showBulkDeleteDialog = true
                                    }
                                },
                                enabled = selectedKeys.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Selected Keys",
                                    tint = if (selectedKeys.isNotEmpty()) BrandCrimson else TextSecondary.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (statusFilter == "LOGGED_IN") "Logged In Users" else "Client Keys",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${users.size} record(s) found",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }

                    if (users.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                isSelectionMode = true
                                selectedKeys = users.map { it.key }.toSet()
                            }
                        ) {
                            Text(
                                text = "Select All",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = com.modxlab.admin.ui.theme.BrandSage,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setUserSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search key or user...",
                        color = TextSecondary.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setUserSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                maxLines = 1,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = com.modxlab.admin.ui.theme.AppSurface,
                    unfocusedContainerColor = com.modxlab.admin.ui.theme.AppSurface,
                    focusedBorderColor = com.modxlab.admin.ui.theme.BrandSage,
                    unfocusedBorderColor = com.modxlab.admin.ui.theme.AppBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_users")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips (Scrollable Row to prevent vertical overflow/stretching)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Keys",
                    "LOGGED_IN" to "Logged In",
                    "ACTIVE" to "Active Only",
                    "INACTIVE" to "Blocked"
                ).forEach { (filterKey, label) ->
                    val isSelected = statusFilter == filterKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setUserStatusFilter(filterKey) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = com.modxlab.admin.ui.theme.BrandSage,
                            selectedLabelColor = Color.White,
                            containerColor = com.modxlab.admin.ui.theme.AppSurfaceVariant,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = com.modxlab.admin.ui.theme.BrandSage,
                            borderColor = com.modxlab.admin.ui.theme.AppBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Users List
            if (users.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "No passkeys",
                            tint = TextTertiary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching records found" else "No users created yet",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
                        )
                        Text(
                            text = "Tap the + button to create a new key",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        items = users,
                        key = { _, item -> item.key },
                        contentType = { _, _ -> "user_card" }
                    ) { index, userItem ->
                        val isSelected = selectedKeys.contains(userItem.key)
                        UserCardItem(
                            index = index + 1,
                            user = userItem,
                            isSelectionMode = isSelectionMode,
                            isSelected = isSelected,
                            onItemClick = {
                                if (isSelectionMode) {
                                    selectedKeys = if (isSelected) {
                                        selectedKeys - userItem.key
                                    } else {
                                        selectedKeys + userItem.key
                                    }
                                } else {
                                    onNavigateToEditUser(userItem.key)
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) {
                                    isSelectionMode = true
                                    selectedKeys = setOf(userItem.key)
                                } else {
                                    selectedKeys = if (isSelected) {
                                        selectedKeys - userItem.key
                                    } else {
                                        selectedKeys + userItem.key
                                    }
                                }
                            },
                            onEditClick = { onNavigateToEditUser(userItem.key) },
                            onDeleteClick = { userToDelete = userItem },
                            onToggleStatusClick = { userToToggle = userItem },
                            onResetHwidClick = { userToResetHwid = userItem },
                            onCopyKey = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("ModX License Key", userItem.key)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToastMessage("License Key Copied")
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (userToDelete != null) {
        val user = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete License User", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)) },
            text = {
                Text(
                    "Are you sure you want to delete user \"${user.user}\" (${user.key})? This will remove their key and login access permanently.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(user.key)
                        userToDelete = null
                    },
                    modifier = Modifier.testTag("dialog_btn_confirm_delete")
                ) {
                    Text("Delete", color = BrandCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = com.modxlab.admin.ui.theme.AppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Toggle Status Dialog
    if (userToToggle != null) {
        val user = userToToggle!!
        val willActivate = !user.isActive
        AlertDialog(
            onDismissRequest = { userToToggle = null },
            title = {
                Text(
                    if (willActivate) "Unblock User / Key" else "Block User / Key",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            },
            text = {
                Text(
                    "Are you sure you want to ${if (willActivate) "unblock / activate" else "block / deactivate"} key access for \"${user.user}\"?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.toggleUserStatus(user.key, willActivate)
                        userToToggle = null
                    },
                    modifier = Modifier.testTag("dialog_btn_confirm_toggle")
                ) {
                    Text(
                        if (willActivate) "Activate" else "Block User",
                        color = if (willActivate) com.modxlab.admin.ui.theme.BrandSage else BrandCrimson,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { userToToggle = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = com.modxlab.admin.ui.theme.AppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Reset HWID Confirmation Dialog
    if (userToResetHwid != null) {
        val user = userToResetHwid!!
        AlertDialog(
            onDismissRequest = { userToResetHwid = null },
            title = { Text("Reset Device Binding", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)) },
            text = {
                Text(
                    "Reset bound device for \"${user.user}\"? This allows the user to log in on a different device.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetUserHwid(user.key)
                        userToResetHwid = null
                    }
                ) {
                    Text("Reset Device", color = com.modxlab.admin.ui.theme.BrandSage, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToResetHwid = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = com.modxlab.admin.ui.theme.AppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Bulk Delete Confirmation Dialog
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = {
                Text(
                    "Delete Selected Keys (${selectedKeys.size})",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete ${selectedKeys.size} selected key(s)? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMultipleUsers(selectedKeys)
                        showBulkDeleteDialog = false
                        isSelectionMode = false
                        selectedKeys = emptySet()
                    }
                ) {
                    Text("Delete All (${selectedKeys.size})", color = BrandCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = com.modxlab.admin.ui.theme.AppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserCardItem(
    index: Int,
    user: UserEntity,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onItemClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatusClick: () -> Unit,
    onResetHwidClick: () -> Unit,
    onCopyKey: () -> Unit
) {
    val isLoggedIn = user.device != "null" && user.device.isNotBlank()

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.10f) else com.modxlab.admin.ui.theme.AppSurface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) com.modxlab.admin.ui.theme.BrandSage else com.modxlab.admin.ui.theme.AppBorder
        ),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onLongClick
            )
            .testTag("user_item_${user.key}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectionMode) {
                    GlassCheckbox(
                        checked = isSelected,
                        onCheckedChange = { onItemClick() }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Index badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(com.modxlab.admin.ui.theme.AppSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Avatar Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isLoggedIn) com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.2f) else com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.Person else Icons.Default.Person,
                        contentDescription = "User",
                        tint = com.modxlab.admin.ui.theme.BrandSage,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // User details
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.user,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = user.key,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = com.modxlab.admin.ui.theme.BrandSage,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        IconButton(
                            onClick = onCopyKey,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Key",
                                tint = TextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Status Badge (Clickable with premium glow to block/unblock)
                StatusBadge(
                    text = if (user.isActive) "ACTIVE" else "BLOCKED",
                    isActive = user.isActive,
                    modifier = Modifier.clickable(onClick = onToggleStatusClick)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logged in device info banner
            if (isLoggedIn) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "Device",
                            tint = com.modxlab.admin.ui.theme.BrandSage,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val loggedInCount = user.device.split(",").size
                        val accessText = if (user.isUnlimitedDevice) "∞" else user.access
                        Text(
                            text = "Logged in: $loggedInCount / $accessText Devices",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "Reset HWID",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = com.modxlab.admin.ui.theme.BrandSage,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onResetHwidClick)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Metadata Chips & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Metadata Chips
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Validity chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Validity",
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user.validity.ifEmpty { "30 Days" },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    // Device Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(com.modxlab.admin.ui.theme.AppSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "Device Access",
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isUnlimitedDevice) "Unlimited (∞)" else "${user.access} Device",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Action Icons: Block, Edit & Delete (Guaranteed visible)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleStatusClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Block User",
                            tint = if (user.isActive) TextSecondary else BrandCrimson,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Key",
                            tint = TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User",
                            tint = BrandCrimson,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
