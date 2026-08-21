package com.modxlab.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.modxlab.admin.ui.viewmodel.AuthViewModel
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
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
        authViewModel.silentLogin()
        
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
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.nature_bg),
                        contentDescription = "Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
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
                        .height(80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The dark background with cutout
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        shape = com.modxlab.admin.ui.components.BottomNavShape(
                            cradleRadius = 24.dp,
                            cradleMargin = 6.dp,
                            cornerRadius = 14.dp,
                            cradleDepth = 24.dp
                        ),
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
                            // Home Button (Classic Home with Emerald Fill Animation)
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
                                    unselectedColor = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Home",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (homeSelected) BrandEmerald else Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        fontWeight = if (homeSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                )
                            }

                            // Update Button (Classic SystemUpdate with Emerald Fill Animation)
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
                                    unselectedColor = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Update",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (updateSelected) BrandEmerald else Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        fontWeight = if (updateSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }

                    // Center Floating Button closely connected to cradle
                    val setPassSelected = currentRoute?.startsWith(Screen.AddUser.route) == true
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset(y = (-16).dp)
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
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2B2B2B))
                                .border(
                                    width = 1.5.dp,
                                    color = if (setPassSelected) BrandEmerald else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Set Pass",
                                tint = if (setPassSelected) BrandEmerald else Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Set Pass",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (setPassSelected) BrandEmerald else Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = if (setPassSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        )
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

