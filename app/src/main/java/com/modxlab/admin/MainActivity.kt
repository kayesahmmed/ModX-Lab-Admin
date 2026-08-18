package com.modxlab.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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
import com.modxlab.admin.ui.screens.AddSellerScreen
import com.modxlab.admin.ui.screens.AddUserScreen
import com.modxlab.admin.ui.screens.DashboardScreen
import com.modxlab.admin.ui.screens.EditSellerScreen
import com.modxlab.admin.ui.screens.EditUserScreen
import com.modxlab.admin.ui.screens.MaintenanceScreen
import com.modxlab.admin.ui.screens.SellerListScreen
import com.modxlab.admin.ui.screens.UserListScreen
import com.modxlab.admin.ui.theme.BrandEmerald
import com.modxlab.admin.ui.theme.CyberBorder
import com.modxlab.admin.ui.theme.CyberSurface
import com.modxlab.admin.ui.theme.ModXAdminTheme
import com.modxlab.admin.ui.theme.TextPrimary
import com.modxlab.admin.ui.theme.TextSecondary
import com.modxlab.admin.ui.viewmodel.AdminViewModel
import com.modxlab.admin.ui.viewmodel.AdminViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this, (application as? android.app.Application)?.let {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        } ?: kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))

        val repository = AdminRepository(
            userDao = database.userDao(),
            sellerDao = database.sellerDao(),
            maintenanceDao = database.maintenanceDao()
        )

        val factory = AdminViewModelFactory(repository)
        val viewModel: AdminViewModel by viewModels { factory }

        setContent {
            ModXAdminTheme {
                MainApp(viewModel = viewModel)
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
    data object Dashboard : BottomNavItem(
        route = Screen.Dashboard.route,
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        testTag = "nav_bottom_dashboard"
    )
    data object Users : BottomNavItem(
        route = Screen.Users.route,
        title = "Users",
        selectedIcon = Icons.Filled.Key,
        unselectedIcon = Icons.Outlined.Key,
        testTag = "nav_bottom_users"
    )
    data object Sellers : BottomNavItem(
        route = Screen.Sellers.route,
        title = "Sellers",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront,
        testTag = "nav_bottom_sellers"
    )
    data object Maintenance : BottomNavItem(
        route = Screen.Maintenance.route,
        title = "Broadcast",
        selectedIcon = Icons.Filled.Campaign,
        unselectedIcon = Icons.Outlined.Campaign,
        testTag = "nav_bottom_maintenance"
    )
}

@Composable
fun MainApp(viewModel: AdminViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Users,
        BottomNavItem.Sellers,
        BottomNavItem.Maintenance
    )

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = isTopLevelDestination,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavigationBar(
                    containerColor = CyberSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    color = if (selected) BrandEmerald else TextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = BrandEmerald,
                                unselectedIconColor = TextSecondary
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
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
                    onNavigateToSellers = { navController.navigate(Screen.Sellers.route) },
                    onNavigateToAddSeller = { navController.navigate(Screen.AddSeller.route) },
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

            composable(Screen.Sellers.route) {
                SellerListScreen(
                    viewModel = viewModel,
                    onNavigateToAddSeller = { navController.navigate(Screen.AddSeller.route) },
                    onNavigateToEditSeller = { key -> navController.navigate(Screen.EditSeller.createRoute(key)) }
                )
            }

            composable(Screen.AddSeller.route) {
                AddSellerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditSeller.route,
                arguments = listOf(navArgument("sellerKey") { type = NavType.StringType })
            ) { backStackEntry ->
                val sellerKey = backStackEntry.arguments?.getString("sellerKey") ?: ""
                EditSellerScreen(
                    sellerKey = sellerKey,
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
