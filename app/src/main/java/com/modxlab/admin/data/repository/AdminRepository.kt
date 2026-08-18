package com.modxlab.admin.data.repository

import com.modxlab.admin.data.local.MaintenanceDao
import com.modxlab.admin.data.local.SellerDao
import com.modxlab.admin.data.local.UserDao
import com.modxlab.admin.data.model.DashboardStats
import com.modxlab.admin.data.model.MaintenanceEntity
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AdminRepository(
    private val userDao: UserDao,
    private val sellerDao: SellerDao,
    private val maintenanceDao: MaintenanceDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allSellers: Flow<List<SellerEntity>> = sellerDao.getAllSellers()
    val maintenanceFlow: Flow<MaintenanceEntity?> = maintenanceDao.getMaintenanceFlow()

    val dashboardStats: Flow<DashboardStats> = combine(
        allUsers,
        allSellers,
        maintenanceFlow
    ) { users, sellers, maintenance ->
        val totalUsers = users.size
        val activeUsers = users.count { it.isActive }
        val inactiveUsers = totalUsers - activeUsers

        val totalSellers = sellers.size
        val activeSellers = sellers.count { it.isActive }
        val inactiveSellers = totalSellers - activeSellers
        val totalCredits = sellers.sumOf { it.coin.toDoubleOrNull() ?: 0.0 }

        DashboardStats(
            totalUsers = totalUsers,
            activeUsers = activeUsers,
            inactiveUsers = inactiveUsers,
            totalSellers = totalSellers,
            activeSellers = activeSellers,
            inactiveSellers = inactiveSellers,
            totalCredits = totalCredits,
            currentVersion = maintenance?.version ?: "1.0.0"
        )
    }

    private fun nowStamp(date: Date = Date()): String {
        val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.US)
        return sdf.format(date)
    }

    private fun generateKey(prefix: String): String {
        val randomPart = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        return "-Nox$prefix$randomPart"
    }

    suspend fun getUserByKey(key: String): UserEntity? = userDao.getUserByKey(key)

    suspend fun addUser(
        username: String,
        password: String,
        access: String,
        validityDays: Int
    ): UserEntity {
        val now = System.currentTimeMillis()
        val expiryMillis = now + (validityDays.toLong() * 24L * 60L * 60L * 1000L)
        val generatedKey = generateKey("User")
        val newUser = UserEntity(
            key = generatedKey,
            user = username.trim(),
            pass = password.trim(),
            status = "true",
            access = access,
            device = "null",
            version = "null",
            rgtime = nowStamp(Date(now)),
            time = expiryMillis.toString(),
            validity = nowStamp(Date(expiryMillis))
        )
        userDao.insertUser(newUser)
        return newUser
    }

    suspend fun updateUserCredentials(key: String, username: String, password: String) {
        userDao.updateUserCredentials(key, username.trim(), password.trim())
    }

    suspend fun toggleUserStatus(key: String, activate: Boolean) {
        userDao.toggleUserStatus(key, if (activate) "true" else "false")
    }

    suspend fun deleteUser(key: String) {
        userDao.deleteByKey(key)
    }

    suspend fun getSellerByKey(key: String): SellerEntity? = sellerDao.getSellerByKey(key)

    suspend fun addSeller(
        username: String,
        password: String,
        access: String,
        coin: String
    ): SellerEntity {
        val generatedKey = generateKey("Seller")
        val newSeller = SellerEntity(
            key = generatedKey,
            user = username.trim(),
            pass = password.trim(),
            status = "true",
            access = access,
            device = "null",
            version = "null",
            coin = coin.trim()
        )
        sellerDao.insertSeller(newSeller)
        return newSeller
    }

    suspend fun updateSellerCredentials(key: String, username: String, password: String) {
        sellerDao.updateSellerCredentials(key, username.trim(), password.trim())
    }

    suspend fun toggleSellerStatus(key: String, activate: Boolean) {
        sellerDao.toggleSellerStatus(key, if (activate) "true" else "false")
    }

    suspend fun deleteSeller(key: String) {
        sellerDao.deleteByKey(key)
    }

    suspend fun setMaintenanceUpdate(version: String, message: String, link: String) {
        val entity = MaintenanceEntity(
            id = "up",
            version = version.trim(),
            message = message.trim(),
            link = link.trim(),
            updatedAt = System.currentTimeMillis()
        )
        maintenanceDao.insertOrUpdate(entity)
    }
}
