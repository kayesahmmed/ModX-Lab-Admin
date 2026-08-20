with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'r') as f:
    content = f.read()

# Replace the entire bottomBar section
start_idx = content.find("bottomBar = {")
end_idx = content.find(") { innerPadding ->", start_idx)

if start_idx != -1 and end_idx != -1:
    new_bottom_bar = """bottomBar = {
            AnimatedVisibility(
                visible = isTopLevelDestination,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .height(80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The dark background with cutout
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = com.modxlab.admin.ui.components.BottomNavShape(cradleRadius = 36.dp, cornerRadius = 24.dp),
                        color = Color(0xFF1E1E1E),
                        shadowElevation = 12.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Home Button
                            val homeSelected = currentRoute == Screen.Dashboard.route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (currentRoute != Screen.Dashboard.route) {
                                            navController.navigate(Screen.Dashboard.route) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                com.modxlab.admin.ui.components.AnimatedFillIcon(
                                    selected = homeSelected,
                                    selectedIcon = Icons.Filled.Home,
                                    unselectedIcon = Icons.Outlined.Home,
                                    selectedColor = BrandEmerald,
                                    unselectedColor = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Home",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (homeSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            // Update Button
                            val updateSelected = currentRoute == Screen.Maintenance.route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        if (currentRoute != Screen.Maintenance.route) {
                                            navController.navigate(Screen.Maintenance.route) {
                                                popUpTo(Screen.Dashboard.route)
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                com.modxlab.admin.ui.components.AnimatedFillIcon(
                                    selected = updateSelected,
                                    selectedIcon = Icons.Filled.SystemUpdate,
                                    unselectedIcon = Icons.Outlined.SystemUpdate,
                                    selectedColor = BrandEmerald,
                                    unselectedColor = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Update",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (updateSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Center Floating Button
                    val setPassSelected = currentRoute?.startsWith(Screen.AddUser.route) == true
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentRoute != Screen.AddUser.route) {
                                    navController.navigate(Screen.AddUser.route) {
                                        popUpTo(Screen.Dashboard.route)
                                        launchSingleTop = true
                                    }
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2D2D2D))
                                .border(
                                    width = 2.dp,
                                    color = if (setPassSelected) BrandEmerald else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.filled.Add,
                                contentDescription = "Set Pass",
                                tint = if (setPassSelected) BrandEmerald else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Set Pass",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (setPassSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }"""
    new_content = content[:start_idx] + new_bottom_bar + "\n    " + content[end_idx:]
    with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'w') as f:
        f.write(new_content)
