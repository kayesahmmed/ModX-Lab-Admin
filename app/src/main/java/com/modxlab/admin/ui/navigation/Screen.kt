package com.modxlab.admin.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Users : Screen("users")
    data object AddUser : Screen("add_user")
    data object EditUser : Screen("edit_user/{userKey}") {
        fun createRoute(userKey: String) = "edit_user/$userKey"
    }
    data object Maintenance : Screen("maintenance")
}
