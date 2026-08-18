package com.modxlab.admin.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.modxlab.admin.ui.theme.BrandCrimson
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandEmeraldLight
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.theme.TextTertiary
import com.modxlab.admin.ui.viewmodel.AdminViewModel

@Composable
fun MaintenanceScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentMaintenance by viewModel.maintenance.collectAsState()

    var version by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var isBroadcasting by remember { mutableStateOf(false) }

    var versionError by remember { mutableStateOf<String?>(null) }
    var messageError by remember { mutableStateOf<String?>(null) }
    var linkError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentMaintenance) {
        currentMaintenance?.let {
            if (version.isEmpty()) version = it.version
            if (message.isEmpty()) message = it.message
            if (link.isEmpty()) link = it.link
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
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
                    text = "System Update",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Broadcast announcements and updates",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Broadcast Form Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandCrimson.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Broadcast",
                            tint = BrandCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Global Broadcast Node (/update/up)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Version Field
                Text(
                    text = "App Target Version",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = version,
                    onValueChange = {
                        version = it
                        if (versionError != null) versionError = null
                    },
                    placeholder = { Text("e.g. 2.4.2", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.SystemUpdate, contentDescription = "Version", tint = BrandCrimson)
                    },
                    isError = versionError != null,
                    supportingText = {
                        if (versionError != null) {
                            Text(versionError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandCrimson,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_maintenance_version")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Message Field
                Text(
                    text = "Announcement Message",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        if (messageError != null) messageError = null
                    },
                    placeholder = { Text("Enter the notice displayed to clients", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Message", tint = BrandCrimson)
                    },
                    isError = messageError != null,
                    supportingText = {
                        if (messageError != null) {
                            Text(messageError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandCrimson,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_maintenance_message")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Link Field
                Text(
                    text = "Update Direct Download Link",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = link,
                    onValueChange = {
                        link = it
                        if (linkError != null) linkError = null
                    },
                    placeholder = { Text("https://example.com/modx-latest.apk", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = "Link", tint = BrandCrimson)
                    },
                    isError = linkError != null,
                    supportingText = {
                        if (linkError != null) {
                            Text(linkError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedBorderColor = BrandCrimson,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_maintenance_link")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        var hasError = false
                        if (version.isBlank()) {
                            versionError = "Version cannot be empty"
                            hasError = true
                        }
                        if (message.isBlank()) {
                            messageError = "Message cannot be empty"
                            hasError = true
                        }
                        if (link.isBlank()) {
                            linkError = "Download link cannot be empty"
                            hasError = true
                        }

                        if (!hasError) {
                            isBroadcasting = true
                            viewModel.setMaintenanceUpdate(
                                version = version,
                                message = message,
                                link = link,
                                onSuccess = {
                                    isBroadcasting = false
                                }
                            )
                        }
                    },
                    enabled = !isBroadcasting,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_broadcast_maintenance")
                ) {
                    if (isBroadcasting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcasting...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Broadcast", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SET & BROADCAST UPDATE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Client Preview Card
        Text(
            text = "Client Dialog Live Preview",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandCrimson.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "Update",
                            tint = BrandCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "New Update Available!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandCrimson.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "v${version.ifEmpty { "2.4.2" }}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandCrimson
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message.ifEmpty { "🔥 System maintenance and security optimizations completed. Please upgrade to the latest build to continue." },
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val targetUri = link.ifEmpty { "https://modxlab.app" }
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUri))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open URL: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Test Link", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Link", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val targetUri = link.ifEmpty { "https://modxlab.app" }
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUri))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open URL: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("UPDATE NOW", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}
