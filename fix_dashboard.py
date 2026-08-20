with open('app/src/main/java/com/modxlab/admin/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Add the missing parts
missing_parts = """
                MetricCard(
                    title = "Total Users",
                    value = stats.totalUsers.toString(),
                    subtitle = "${stats.activeUsers} active",
                    icon = Icons.Default.Group,
                    color = BrandEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToUsers
                )
            }
        }

        // Quick Actions section
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Client Access",
                    subtitle = "Generate & manage client keys",
                    icon = Icons.Default.Key,
                    badgeText = "${stats.totalUsers} Active",
                    onCardClick = onNavigateToUsers,
                    onActionClick = onNavigateToAddUser,
                    actionLabel = "Add Client",
                    color1 = BrandEmerald,
                    color2 = BrandCyan
                )

                QuickActionCard(
                    title = "Maintenance",
                    subtitle = "App update & announcements",
                    icon = Icons.Default.SystemUpdate,
                    badgeText = maintenance?.version ?: "V1.0",
                    onCardClick = onNavigateToMaintenance,
                    onActionClick = onNavigateToMaintenance,
                    actionLabel = "Configure",
                    color1 = BrandAmber,
                    color2 = BrandCrimson
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit,
    actionLabel: String,
    color1: Color,
    color2: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onCardClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(color1.copy(alpha = 0.25f), color2.copy(alpha = 0.15f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color1,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White.copy(alpha = 0.75f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .testTag("action_$title")
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
"""
with open('app/src/main/java/com/modxlab/admin/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content + missing_parts)
