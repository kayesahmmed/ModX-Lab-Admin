with open('app/src/main/java/com/modxlab/admin/data/model/Models.kt', 'r') as f:
    content = f.read()

content = content.replace(
    """data class DashboardStats(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val inactiveUsers: Int = 0,""",
    """data class DashboardStats(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val inactiveUsers: Int = 0,
    val totalKeys: Int = 0,
    val loggedInUsers: Int = 0,"""
)

with open('app/src/main/java/com/modxlab/admin/data/model/Models.kt', 'w') as f:
    f.write(content)
