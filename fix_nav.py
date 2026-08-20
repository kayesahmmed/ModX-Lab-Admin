with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'r') as f:
    content = f.read()

# Make isTopLevelDestination include AddUser
content = content.replace(
    "val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }",
    """val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute } || currentRoute?.startsWith(Screen.AddUser.route) == true || currentRoute?.startsWith(Screen.Users.route) == true"""
)

# Update Center Floating Button to navigate to AddUser
content = content.replace(
    "val setPassSelected = currentRoute == Screen.Users.route",
    "val setPassSelected = currentRoute?.startsWith(Screen.AddUser.route) == true"
)
content = content.replace(
    """navController.navigate(Screen.Users.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }""",
    """navController.navigate(Screen.AddUser.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }"""
)

# Fix clickable area for Home Button by putting padding BEFORE clickable
content = content.replace(
    """                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .padding(8.dp)""",
    """                                    .padding(8.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }"""
)

# Fix clickable area for Update Button
content = content.replace(
    """                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(Screen.Maintenance.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .padding(8.dp)""",
    """                                    .padding(8.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(Screen.Maintenance.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }"""
)

# Fix clickable area for + Button
content = content.replace(
    """                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                navController.navigate(Screen.AddUser.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }""",
    """                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                navController.navigate(Screen.AddUser.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(8.dp)"""
)

with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'w') as f:
    f.write(content)
