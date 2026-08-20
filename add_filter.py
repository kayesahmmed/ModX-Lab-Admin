with open('app/src/main/java/com/modxlab/admin/ui/screens/UserListScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    """                listOf(
                    "ALL" to "All Passes",
                    "ACTIVE" to "Active Only",
                    "INACTIVE" to "Deactivated"
                ).forEach { (filterKey, label) ->""",
    """                listOf(
                    "ALL" to "All Passes",
                    "LOGGED_IN" to "Logged In",
                    "ACTIVE" to "Active Only",
                    "INACTIVE" to "Deactivated"
                ).forEach { (filterKey, label) ->"""
)

with open('app/src/main/java/com/modxlab/admin/ui/screens/UserListScreen.kt', 'w') as f:
    f.write(content)
