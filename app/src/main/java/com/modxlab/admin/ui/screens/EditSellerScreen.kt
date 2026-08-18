package com.modxlab.admin.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Storefront
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
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.ui.theme.BrandAmber
import com.modxlab.admin.ui.theme.BrandCrimson
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandEmeraldLight
import com.modxlab.admin.ui.theme.BrandIndigo
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.StatusActive
import com.modxlab.admin.ui.theme.StatusInactive
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.theme.TextTertiary
import com.modxlab.admin.ui.viewmodel.AdminViewModel

@Composable
fun EditSellerScreen(
    sellerKey: String,
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var sellerEntity by remember { mutableStateOf<SellerEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sellerKey) {
        val s = viewModel.getSeller(sellerKey)
        if (s != null) {
            sellerEntity = s
            username = s.user
            password = s.pass
        }
        isLoading = false
    }

    val scrollState = rememberScrollState()

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandIndigo)
        }
        return
    }

    if (sellerEntity == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Seller not found", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val seller = sellerEntity!!

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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "Edit Reseller",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Reseller key: ${seller.key}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandCrimson.copy(alpha = 0.15f))
                    .testTag("btn_delete_seller_screen")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Seller",
                    tint = BrandCrimson
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Key Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CyberSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
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
                            .background(BrandIndigo.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Key",
                            tint = BrandIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SELLER KEY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = seller.key,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandCyan
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ModX Seller Key", seller.key)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Key copied to clipboard", Toast.LENGTH_SHORT).show()
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

        // Form Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Reseller Credentials",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

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
                    onValueChange = { username = it },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Username", tint = BrandIndigo)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandIndigo,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_input_seller_username")
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    onValueChange = { password = it },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password", tint = BrandIndigo)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandIndigo,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_input_seller_password")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isSaving = true
                        viewModel.updateSellerCredentials(
                            key = seller.key,
                            user = username,
                            pass = password,
                            onSuccess = {
                                isSaving = false
                                sellerEntity = sellerEntity?.copy(user = username, pass = password)
                            }
                        )
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_save_seller_changes")
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE RESELLER CREDENTIALS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Activation Toggle Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (seller.isActive) BrandEmerald.copy(alpha = 0.2f) else BrandCrimson.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Status",
                            tint = if (seller.isActive) BrandEmeraldLight else BrandCrimson
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (seller.isActive) "Reseller Portal Active" else "Reseller Deactivated",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (seller.isActive) StatusActive else StatusInactive
                            )
                        )
                        Text(
                            text = if (seller.isActive) "Able to create sub-licenses & distribute" else "Portal access suspended",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        )
                    }
                }

                Switch(
                    checked = seller.isActive,
                    onCheckedChange = { willActivate ->
                        viewModel.toggleSellerStatus(seller.key, willActivate)
                        sellerEntity = sellerEntity?.copy(status = if (willActivate) "true" else "false")
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BrandEmerald,
                        checkedTrackColor = BrandEmerald.copy(alpha = 0.3f),
                        uncheckedThumbColor = BrandCrimson,
                        uncheckedTrackColor = BrandCrimson.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("switch_seller_status")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware & Access Info
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Reseller Telemetry & Access",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceVariant)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Balance",
                        tint = BrandAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Credit Balance Allocated",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        )
                        Text(
                            text = "$${seller.coin} USD",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BrandAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceVariant)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Device",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Bound Hardware Device",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        )
                        Text(
                            text = if (seller.device == "null" || seller.device.isBlank()) "Not Authorized Yet (null)" else seller.device,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontSize = 13.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceVariant)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = "Version",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Client Version",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        )
                        Text(
                            text = if (seller.version == "null" || seller.version.isBlank()) "Not Registered Yet (null)" else seller.version,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontSize = 13.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceVariant)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = "Device Access",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Device Access Restriction",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        )
                        Text(
                            text = if (seller.isUnlimitedDevice) "Unlimited Devices (∞)" else "1 Device HWID Locked",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontSize = 13.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Seller Account", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Text(
                    "Are you sure you want to delete reseller \"${seller.user}\"? They will lose access to the reseller portal.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSeller(seller.key)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = BrandCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.8f))
                }
            },
            containerColor = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
