package com.modxlab.admin.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val key: String,
    val user: String,
    val pass: String,
    val status: String = "true", // "true" (active) or "false" (deactivated)
    val access: String = "1", // "1" (Single Device) or "∞" (Unlimited)
    val device: String = "null",
    val version: String = "null",
    val rgtime: String = "",
    val time: String = "", // expiry timestamp in millis
    val validity: String = "" // formatted validity string (e.g. "30 Days" or "18:00 - 25/08/2026")
) {
    val isActive: Boolean get() = status.equals("true", ignoreCase = true)
    val isUnlimitedDevice: Boolean get() = access == "∞"
}

@Entity(tableName = "sellers")
data class SellerEntity(
    @PrimaryKey val key: String,
    val user: String,
    val pass: String,
    val status: String = "true",
    val access: String = "1",
    val device: String = "null",
    val version: String = "null",
    val coin: String = "50" // Credit balance in USD
) {
    val isActive: Boolean get() = status.equals("true", ignoreCase = true)
    val isUnlimitedDevice: Boolean get() = access == "∞"
}

@Entity(tableName = "maintenance")
data class MaintenanceEntity(
    @PrimaryKey val id: String = "up",
    val version: String = "1.0.0",
    val message: String = "System operational. All servers running normally.",
    val link: String = "https://modxlab.app/downloads/latest",
    val updatedAt: Long = System.currentTimeMillis()
)

data class DashboardStats(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val inactiveUsers: Int = 0,
    val totalKeys: Int = 0,
    val loggedInUsers: Int = 0,
    val totalSellers: Int = 0,
    val activeSellers: Int = 0,
    val inactiveSellers: Int = 0,
    val totalCredits: Double = 0.0,
    val currentVersion: String = "1.0.0"
)
