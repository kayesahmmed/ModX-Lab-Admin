package com.modxlab.admin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.modxlab.admin.ui.theme.BrandAmber
import com.modxlab.admin.ui.theme.BrandAmberDark
import com.modxlab.admin.ui.theme.BrandCrimson
import com.modxlab.admin.ui.theme.BrandCrimsonDark
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandEmeraldDark
import com.modxlab.admin.ui.theme.BrandEmeraldLight
import com.modxlab.admin.ui.theme.BrandIndigo
import com.modxlab.admin.ui.theme.BrandIndigoDark
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.StatusActive
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.theme.TextTertiary
import com.modxlab.admin.ui.components.GlassBox
import com.modxlab.admin.ui.components.GlassCard
import com.modxlab.admin.ui.components.GlassBackgroundBrush
import com.modxlab.admin.ui.components.GlassBorderBrush
import com.modxlab.admin.ui.viewmodel.AdminViewModel

@Composable
fun DashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToUsers: () -> Unit,
    onNavigateToAddUser: () -> Unit,
    onNavigateToSellers: () -> Unit,
    onNavigateToAddSeller: () -> Unit,
    onNavigateToMaintenance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val maintenance by viewModel.maintenance.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header & Security Badge
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Panel",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "System Overview",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f)),
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandEmerald.copy(alpha = 0.15f))
                            .border(1.dp, BrandEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ADMIN ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandEmeraldLight
                                )
                            )
                        }
                    }
                }
            }
        }

        // Security Hardening Info Banner
        item {
            GlassBox(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Backend Security",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "System database is secure and protected.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
        }

        // Live Metric Counters
        item {
            Text(
                text = "Live Overview",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Users",
                    value = stats.totalUsers.toString(),
                    subtitle = "${stats.activeUsers} active",
                    icon = Icons.Default.Group,
                    color = BrandEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToUsers
                )
                MetricCard(
                    title = "Resellers",
                    value = stats.totalSellers.toString(),
                    subtitle = "${stats.activeSellers} active",
                    icon = Icons.Default.Storefront,
                    color = BrandIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSellers
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Reseller Credits",
                    value = "$${stats.totalCredits.toInt()}",
                    subtitle = "Allocated Balance",
                    icon = Icons.Default.Paid,
                    color = BrandAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSellers
                )
                MetricCard(
                    title = "App Version",
                    value = stats.currentVersion,
                    subtitle = "Broadcast Online",
                    icon = Icons.Default.SystemUpdate,
                    color = BrandCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMaintenance
                )
            }
        }

        // Main Navigation Gradient Cards (Directly matching original Sketchware architecture with modern Compose Polish)
        item {
            Text(
                text = "Management",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // 1. User Pass Management Card (Green gradient in original)
        item {
            GradientActionCard(
                title = "Set Pass & Keys",
                subtitle = "Manage user accounts, passkeys & device HWID binding",
                icon = Icons.Default.Key,
                startColor = Color(0xFF00875A),
                endColor = Color(0xFF00C853),
                badgeText = "${stats.totalUsers} Keys",
                onCardClick = onNavigateToUsers,
                onActionClick = onNavigateToAddUser,
                actionLabel = "Add Pass",
                testTag = "btn_nav_users"
            )
        }

        // 2. Maintenance & Updates (Red gradient in original)
        item {
            GradientActionCard(
                title = "System Update",
                subtitle = "Broadcast update announcements, version & download links",
                icon = Icons.Default.Campaign,
                startColor = Color(0xFFC62828),
                endColor = Color(0xFFFF1744),
                badgeText = "v${stats.currentVersion}",
                onCardClick = onNavigateToMaintenance,
                onActionClick = onNavigateToMaintenance,
                actionLabel = "Update",
                testTag = "btn_nav_maintenance"
            )
        }

        // 3. Resellers / Sellers (Indigo gradient in original)
        item {
            GradientActionCard(
                title = "Seller Management",
                subtitle = "Manage sellers and credit allocations",
                icon = Icons.Default.Storefront,
                startColor = Color(0xFF3949AB),
                endColor = Color(0xFF5C6BC0),
                badgeText = "${stats.totalSellers} Sellers",
                onCardClick = onNavigateToSellers,
                onActionClick = onNavigateToAddSeller,
                actionLabel = "Add Seller",
                testTag = "btn_nav_sellers"
            )
        }

        // Broadcast Info Card
        if (maintenance != null) {
            item {
                GlassCard(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcement",
                            tint = BrandCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Current Update (v${maintenance?.version})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = maintenance?.message ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = maintenance?.link ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BrandCyan,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    GlassBox(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun GradientActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color,
    badgeText: String,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit,
    actionLabel: String,
    testTag: String
) {
    GlassCard(
        shape = RoundedCornerShape(22.dp),
        onClick = onCardClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.20f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = startColor
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("${testTag}_action")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = actionLabel,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
