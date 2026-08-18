package com.modxlab.admin.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Users : Screen("users")
    data object AddUser : Screen("add_user")
    data object EditUser : Screen("edit_user/{userKey}") {
        fun createRoute(userKey: String) = "edit_user/$userKey"
    }
    data object Sellers : Screen("sellers")
    data object AddSeller : Screen("add_seller")
    data object EditSeller : Screen("edit_seller/{sellerKey}") {
        fun createRoute(sellerKey: String) = "edit_seller/$sellerKey"
    }
    data object Maintenance : Screen("maintenance")
}
