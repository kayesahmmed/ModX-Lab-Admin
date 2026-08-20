with open('app/src/main/java/com/modxlab/admin/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

old_metrics = """            Row(
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
            }"""

new_metrics = """            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Users",
                    value = stats.loggedInUsers.toString(),
                    subtitle = "Logged in devices",
                    icon = Icons.Default.Group,
                    color = BrandEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToUsers
                )
                MetricCard(
                    title = "Total Keys",
                    value = stats.totalKeys.toString(),
                    subtitle = "${stats.activeUsers} active",
                    icon = Icons.Default.VpnKey,
                    color = BrandCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToUsers
                )
            }"""

if "import androidx.compose.material.icons.filled.VpnKey" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Group", "import androidx.compose.material.icons.filled.Group\nimport androidx.compose.material.icons.filled.VpnKey")

content = content.replace(old_metrics, new_metrics)

with open('app/src/main/java/com/modxlab/admin/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
