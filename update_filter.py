with open('app/src/main/java/com/modxlab/admin/ui/viewmodel/AdminViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace(
    """            val matchesFilter = when (filter) {
                "ACTIVE" -> user.isActive
                "INACTIVE" -> !user.isActive
                else -> true
            }""",
    """            val matchesFilter = when (filter) {
                "ACTIVE" -> user.isActive
                "INACTIVE" -> !user.isActive
                "LOGGED_IN" -> user.device != "null" && user.device.isNotBlank()
                else -> true
            }"""
)

with open('app/src/main/java/com/modxlab/admin/ui/viewmodel/AdminViewModel.kt', 'w') as f:
    f.write(content)
