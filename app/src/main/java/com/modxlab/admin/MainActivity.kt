package com.modxlab.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.modxlab.admin.data.local.AppDatabase
import com.modxlab.admin.data.repository.AdminRepository
import com.modxlab.admin.ui.navigation.Screen
import com.modxlab.admin.ui.screens.AddUserScreen
import com.modxlab.admin.ui.screens.DashboardScreen
import com.modxlab.admin.ui.screens.EditUserScreen
import com.modxlab.admin.ui.screens.MaintenanceScreen
import com.modxlab.admin.ui.screens.UserListScreen
import com.modxlab.admin.ui.theme.BrandCyan
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.BrandEmeraldLight
import com.modxlab.admin.ui.theme.CyberBg
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberBorderLight
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.CyberSurfaceVariant
import com.modxlab.admin.ui.theme.ModXAdminTheme
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.viewmodel.AdminViewModel
import com.modxlab.admin.ui.viewmodel.AdminViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Runtime Security Check to prevent repackaging
        if (packageName != "com.kayesahmmed.admin") {
            finishAffinity()
            return
        }

        enableEdgeToEdge()

        val factory = AdminViewModelFactory(
            AdminRepository(
                userDao = AppDatabase.getDatabase(this, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)).userDao(),
                sellerDao = AppDatabase.getDatabase(this, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)).sellerDao(),
                maintenanceDao = AppDatabase.getDatabase(this, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)).maintenanceDao()
            )
        )
        val viewModel: AdminViewModel by viewModels { factory }

        setContent {
            ModXAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.modxlab.admin.ui.theme.AppBg
                ) {
                    MainApp(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    data object Home : BottomNavItem(
        route = Screen.Dashboard.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_bottom_home"
    )
    data object SetPass : BottomNavItem(
        route = Screen.Users.route,
        title = "Set Pass",
        selectedIcon = Icons.Filled.Key,
        unselectedIcon = Icons.Outlined.Key,
        testTag = "nav_bottom_set_pass"
    )
    data object Update : BottomNavItem(
        route = Screen.Maintenance.route,
        title = "Update",
        selectedIcon = Icons.Filled.SystemUpdate,
        unselectedIcon = Icons.Outlined.SystemUpdate,
        testTag = "nav_bottom_update"
    )
}

@Composable
fun MainApp(viewModel: AdminViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    val appBrandingToast = com.modxlab.admin.core.SecurityCore.getSignatureMessage()

    LaunchedEffect(Unit) {
        viewModel.showToastMessage(appBrandingToast)
    }

    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.SetPass,
        BottomNavItem.Update
    )

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute } || currentRoute?.startsWith(Screen.AddUser.route) == true || currentRoute?.startsWith(Screen.Users.route) == true

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                com.modxlab.admin.ui.components.PremiumSnackbarHost(snackbarData = snackbarData)
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isTopLevelDestination,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .height(82.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The light sage background with cutout and clear border stroke matching organic palette
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        shape = com.modxlab.admin.ui.components.BottomNavShape(
                            cradleRadius = 27.dp,
                            shoulderRadius = 12.dp,
                            cradleDepth = 30.dp,
                            cornerRadius = 18.dp
                        ),
                        color = com.modxlab.admin.ui.theme.AppSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.2.dp,
                            color = com.modxlab.admin.ui.theme.BrandSage.copy(alpha = 0.60f)
                        ),
                        shadowElevation = 10.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 36.dp),
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
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                com.modxlab.admin.ui.components.AnimatedFillIcon(
                                    selected = homeSelected,
                                    selectedIcon = Icons.Filled.Home,
                                    unselectedIcon = Icons.Outlined.Home,
                                    selectedColor = com.modxlab.admin.ui.theme.BrandSage,
                                    unselectedColor = com.modxlab.admin.ui.theme.TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Home",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (homeSelected) com.modxlab.admin.ui.theme.TextPrimary else com.modxlab.admin.ui.theme.TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (homeSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
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
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                com.modxlab.admin.ui.components.AnimatedFillIcon(
                                    selected = updateSelected,
                                    selectedIcon = Icons.Filled.SystemUpdate,
                                    unselectedIcon = Icons.Outlined.SystemUpdate,
                                    selectedColor = com.modxlab.admin.ui.theme.BrandSage,
                                    unselectedColor = com.modxlab.admin.ui.theme.TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Update",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (updateSelected) com.modxlab.admin.ui.theme.TextPrimary else com.modxlab.admin.ui.theme.TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (updateSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    // Center Section: Floating FAB + Elevated "Set Pass" Label
                    val setPassSelected = currentRoute?.startsWith(Screen.AddUser.route) == true
                    
                    // Floating Action Button docked into top cutout
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 2.dp)
                            .size(46.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(com.modxlab.admin.ui.theme.BrandDarkCharcoal)
                            .border(
                                width = 1.8.dp,
                                color = com.modxlab.admin.ui.theme.BrandSage,
                                shape = CircleShape
                            )
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
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Set Pass",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // "Set Pass" Label positioned higher up inside bottom bar
                    Text(
                        text = "Set Pass",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (setPassSelected) com.modxlab.admin.ui.theme.TextPrimary else com.modxlab.admin.ui.theme.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (setPassSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
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
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToUsers = { navController.navigate(Screen.Users.route) },
                    onNavigateToAddUser = { navController.navigate(Screen.AddUser.route) },
                    onNavigateToMaintenance = { navController.navigate(Screen.Maintenance.route) }
                )
            }

            composable(Screen.Users.route) {
                UserListScreen(
                    viewModel = viewModel,
                    onNavigateToAddUser = { navController.navigate(Screen.AddUser.route) },
                    onNavigateToEditUser = { key -> navController.navigate(Screen.EditUser.createRoute(key)) }
                )
            }

            composable(Screen.AddUser.route) {
                AddUserScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditUser.route,
                arguments = listOf(navArgument("userKey") { type = NavType.StringType })
            ) { backStackEntry ->
                val userKey = backStackEntry.arguments?.getString("userKey") ?: ""
                EditUserScreen(
                    userKey = userKey,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Maintenance.route) {
                MaintenanceScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

