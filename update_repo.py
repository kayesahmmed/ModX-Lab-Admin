with open('app/src/main/java/com/modxlab/admin/data/repository/AdminRepository.kt', 'r') as f:
    content = f.read()

content = content.replace(
    """        val totalUsers = users.size
        val activeUsers = users.count { it.isActive }
        val inactiveUsers = totalUsers - activeUsers""",
    """        val totalKeys = users.size
        val loggedInUsers = users.count { it.device != "null" && it.device.isNotBlank() }
        val totalUsers = loggedInUsers
        val activeUsers = users.count { it.isActive }
        val inactiveUsers = totalKeys - activeUsers"""
)

content = content.replace(
    """        DashboardStats(
            totalUsers = totalUsers,
            activeUsers = activeUsers,
            inactiveUsers = inactiveUsers,""",
    """        DashboardStats(
            totalUsers = totalUsers,
            activeUsers = activeUsers,
            inactiveUsers = inactiveUsers,
            totalKeys = totalKeys,
            loggedInUsers = loggedInUsers,"""
)

with open('app/src/main/java/com/modxlab/admin/data/repository/AdminRepository.kt', 'w') as f:
    f.write(content)
