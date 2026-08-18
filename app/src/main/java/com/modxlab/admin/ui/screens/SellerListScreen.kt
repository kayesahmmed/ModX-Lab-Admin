package com.modxlab.admin.ui.screens
import dev.chrisbanes.haze.hazeChild

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
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
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.ui.theme.BrandAmber
import com.modxlab.admin.ui.theme.BrandCrimson
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandIndigo
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
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
fun SellerListScreen(
    viewModel: AdminViewModel,
    onNavigateToAddSeller: () -> Unit,
    onNavigateToEditSeller: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sellers by viewModel.filteredSellers.collectAsState()
    val searchQuery by viewModel.sellerSearchQuery.collectAsState()
    val statusFilter by viewModel.sellerStatusFilter.collectAsState()

    var sellerToDelete by remember { mutableStateOf<SellerEntity?>(null) }
    var sellerToToggle by remember { mutableStateOf<SellerEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddSeller,
                containerColor = BrandIndigo,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("fab_add_seller")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Seller")
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

            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Authorized Resellers",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${sellers.size} registered seller accounts",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSellerSearchQuery(it) },
                placeholder = { Text("Search by username, key...", color = Color.White.copy(alpha = 0.6f)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSellerSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    focusedBorderColor = BrandIndigo,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_sellers")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Sellers",
                    "ACTIVE" to "Active Only",
                    "INACTIVE" to "Deactivated"
                ).forEach { (filterKey, label) ->
                    val isSelected = statusFilter == filterKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSellerStatusFilter(filterKey) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandIndigo.copy(alpha = 0.25f),
                            selectedLabelColor = Color.White,
                            containerColor = Color.Black.copy(alpha = 0.5f),
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = BrandIndigo,
                            borderColor = CyberBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sellers List
            if (sellers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "No sellers",
                            tint = TextTertiary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching resellers found" else "No resellers registered yet",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                        Text(
                            text = "Tap the + button to onboard a new reseller",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.6f))
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(sellers, key = { _, item -> item.key }) { index, sellerItem ->
                        SellerCardItem(
                            index = index + 1,
                            seller = sellerItem,
                            onEditClick = { onNavigateToEditSeller(sellerItem.key) },
                            onDeleteClick = { sellerToDelete = sellerItem },
                            onToggleStatusClick = { sellerToToggle = sellerItem },
                            onCopyKey = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("ModX Seller Key", sellerItem.key)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Seller Key Copied", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (sellerToDelete != null) {
        val seller = sellerToDelete!!
        AlertDialog(
            onDismissRequest = { sellerToDelete = null },
            title = { Text("Delete Seller Account", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Text(
                    "Are you sure you want to delete reseller \"${seller.user}\" (${seller.key})? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSeller(seller.key)
                        sellerToDelete = null
                    },
                    modifier = Modifier.testTag("dialog_btn_confirm_delete_seller")
                ) {
                    Text("Delete", color = BrandCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { sellerToDelete = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.8f))
                }
            },
            containerColor = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Toggle Status Dialog
    if (sellerToToggle != null) {
        val seller = sellerToToggle!!
        val willActivate = !seller.isActive
        AlertDialog(
            onDismissRequest = { sellerToToggle = null },
            title = {
                Text(
                    if (willActivate) "Activate Seller" else "Deactivate Seller",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    "Are you sure you want to ${if (willActivate) "activate" else "deactivate"} reseller \"${seller.user}\"?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.toggleSellerStatus(seller.key, willActivate)
                        sellerToToggle = null
                    },
                    modifier = Modifier.testTag("dialog_btn_confirm_toggle_seller")
                ) {
                    Text(
                        if (willActivate) "Activate" else "Deactivate",
                        color = if (willActivate) BrandEmerald else BrandCrimson,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { sellerToToggle = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.8f))
                }
            },
            containerColor = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SellerCardItem(
    index: Int,
    seller: SellerEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatusClick: () -> Unit,
    onCopyKey: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .hazeChild(
                state = com.modxlab.admin.LocalHazeState.current,
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = onEditClick,
                onLongClick = onToggleStatusClick
            )
            .testTag("seller_item_${seller.key}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Index badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Avatar Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandIndigo.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Seller",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Seller details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = seller.user,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = seller.key,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandCyan,
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
                                tint = TextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Status Badge (Clickable)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (seller.isActive) StatusActiveBg else StatusInactiveBg)
                        .border(
                            1.dp,
                            if (seller.isActive) StatusActive else StatusInactive,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onToggleStatusClick)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (seller.isActive) "ACTIVE" else "DEACTIVATED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (seller.isActive) StatusActive else StatusInactive
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Credit chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandAmber.copy(alpha = 0.15f))
                        .border(1.dp, BrandAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Credits",
                        tint = BrandAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$${seller.coin} Credits",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BrandAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // Device Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = "Device Access",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (seller.isUnlimitedDevice) "Unlimited (∞)" else "1 Device",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Icons
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Seller",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Seller",
                        tint = BrandCrimson.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
