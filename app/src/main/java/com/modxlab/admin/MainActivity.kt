package com.modxlab.admin
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeState

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.modxlab.admin.ui.screens.AddSellerScreen
import com.modxlab.admin.ui.screens.AddUserScreen
import com.modxlab.admin.ui.screens.DashboardScreen
import com.modxlab.admin.ui.screens.EditSellerScreen
import com.modxlab.admin.ui.screens.EditUserScreen
import com.modxlab.admin.ui.screens.MaintenanceScreen
import com.modxlab.admin.ui.screens.SellerListScreen
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

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.modxlab.admin.R

val LocalHazeState = compositionLocalOf { dev.chrisbanes.haze.HazeState() }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val hazeState = remember { dev.chrisbanes.haze.HazeState() }
                CompositionLocalProvider(LocalHazeState provides hazeState) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.nature_bg),
                            contentDescription = "Background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .haze(
                                    state = hazeState,
                                    style = dev.chrisbanes.haze.HazeStyle(
                                        tint = Color.Black.copy(alpha = 0.2f),
                                        blurRadius = 16.dp
                                    )
                                )
                        )
                        MainApp(viewModel = viewModel)
                    }
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

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomNavItems.forEach { item ->
                                val selected = currentRoute == item.route

                                val iconScale by animateFloatAsState(
                                    targetValue = if (selected) 1.1f else 1.0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    label = "iconScale_${item.title}"
                                )

                                val pillBgColor by animateColorAsState(
                                    targetValue = if (selected) Color(0xFFE3F2FD) else Color.Transparent,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    label = "pillBg_${item.title}"
                                )

                                val contentColor by animateColorAsState(
                                    targetValue = if (selected) Color(0xFF1976D2) else Color(0xFF424242),
                                    label = "contentColor_${item.title}"
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(pillBgColor)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                        .padding(horizontal = 24.dp, vertical = 8.dp)
                                        .testTag(item.testTag),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title,
                                            tint = contentColor,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .scale(iconScale)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = contentColor
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
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

