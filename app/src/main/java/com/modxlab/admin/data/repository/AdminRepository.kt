package com.modxlab.admin.data.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.modxlab.admin.data.local.MaintenanceDao
import com.modxlab.admin.data.local.SellerDao
import com.modxlab.admin.data.local.UserDao
import com.modxlab.admin.data.model.DashboardStats
import com.modxlab.admin.data.model.MaintenanceEntity
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AdminRepository(
    private val userDao: UserDao,
    private val sellerDao: SellerDao,
    private val maintenanceDao: MaintenanceDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val firebaseDatabase: FirebaseDatabase by lazy {
        try {
            FirebaseDatabase.getInstance("https://kayes-ahmmed-pro-default-rtdb.firebaseio.com")
        } catch (e: Exception) {
            FirebaseDatabase.getInstance()
        }
    }

    private val userRef by lazy { firebaseDatabase.getReference("User") }
    private val sellerRef by lazy { firebaseDatabase.getReference("Seller") }
    private val maintenanceRef by lazy { firebaseDatabase.getReference("update/up") }
    private val legacyUpRef by lazy { firebaseDatabase.getReference("up") }

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

    init {
        setupFirebaseRealtimeSync()
    }

    private fun setupFirebaseRealtimeSync() {
        try {
            // Sync Users from Firebase Realtime Database
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val userList = mutableListOf<UserEntity>()
                        for (child in snapshot.children) {
                            val key = child.key ?: child.child("key").getValue(String::class.java) ?: continue
                            val user = child.child("user").getValue(String::class.java) ?: ""
                            val pass = child.child("pass").getValue(String::class.java) ?: ""
                            val status = child.child("status").getValue(String::class.java) ?: "true"
                            val access = child.child("access").getValue(String::class.java) ?: "1"
                            val device = child.child("device").getValue(String::class.java) ?: "null"
                            val version = child.child("version").getValue(String::class.java) ?: "null"
                            val rgtime = child.child("rgtime").getValue(String::class.java) ?: ""
                            val time = child.child("time").getValue(String::class.java) ?: ""
                            val validity = child.child("Validity").getValue(String::class.java)
                                ?: child.child("validity").getValue(String::class.java) ?: ""

                            userList.add(
                                UserEntity(
                                    key = key,
                                    user = user,
                                    pass = pass,
                                    status = status,
                                    access = access,
                                    device = device,
                                    version = version,
                                    rgtime = rgtime,
                                    time = time,
                                    validity = validity
                                )
                            )
                        }

                        if (userList.isNotEmpty()) {
                            scope.launch(Dispatchers.IO) {
                                userDao.insertUsers(userList)
                                userDao.deleteUsersNotIn(userList.map { it.key })
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AdminRepository", "Error processing Firebase users: ${e.message}", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AdminRepository", "Firebase User listener error: ${error.message}")
                }
            })

            // Sync Sellers from Firebase Realtime Database
            sellerRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val sellerList = mutableListOf<SellerEntity>()
                        for (child in snapshot.children) {
                            val key = child.key ?: child.child("key").getValue(String::class.java) ?: continue
                            val user = child.child("user").getValue(String::class.java) ?: ""
                            val pass = child.child("pass").getValue(String::class.java) ?: ""
                            val status = child.child("status").getValue(String::class.java) ?: "true"
                            val access = child.child("access").getValue(String::class.java) ?: "1"
                            val device = child.child("device").getValue(String::class.java) ?: "null"
                            val version = child.child("version").getValue(String::class.java) ?: "null"
                            val coin = child.child("coin").getValue(String::class.java) ?: "0"

                            sellerList.add(
                                SellerEntity(
                                    key = key,
                                    user = user,
                                    pass = pass,
                                    status = status,
                                    access = access,
                                    device = device,
                                    version = version,
                                    coin = coin
                                )
                            )
                        }

                        if (sellerList.isNotEmpty()) {
                            scope.launch(Dispatchers.IO) {
                                sellerDao.insertSellers(sellerList)
                                sellerDao.deleteSellersNotIn(sellerList.map { it.key })
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AdminRepository", "Error processing Firebase sellers: ${e.message}", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AdminRepository", "Firebase Seller listener error: ${error.message}")
                }
            })

            // Sync Maintenance from Firebase Realtime Database
            val maintenanceListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val version = snapshot.child("version").getValue(String::class.java) ?: "1.0.0"
                            val message = snapshot.child("message").getValue(String::class.java) ?: ""
                            val link = snapshot.child("link").getValue(String::class.java) ?: ""

                            scope.launch(Dispatchers.IO) {
                                maintenanceDao.insertOrUpdate(
                                    MaintenanceEntity(
                                        id = "up",
                                        version = version,
                                        message = message,
                                        link = link,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AdminRepository", "Error processing Firebase maintenance: ${e.message}", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("AdminRepository", "Firebase Maintenance listener error: ${error.message}")
                }
            }

            maintenanceRef.addValueEventListener(maintenanceListener)
            legacyUpRef.addValueEventListener(maintenanceListener)
        } catch (e: Exception) {
            Log.e("AdminRepository", "Firebase setup failed: ${e.message}", e)
        }
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

        // Save locally in Room
        userDao.insertUser(newUser)

        // Save directly in Firebase Realtime Database
        val map = hashMapOf<String, Any>(
            "user" to newUser.user,
            "pass" to newUser.pass,
            "status" to "true",
            "access" to newUser.access,
            "key" to newUser.key,
            "device" to "null",
            "version" to "null",
            "rgtime" to newUser.rgtime,
            "time" to newUser.time,
            "Validity" to newUser.validity
        )

        try {
            userRef.child(generatedKey).setValue(map).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to write user to Firebase: ${e.message}", e)
        }

        return newUser
    }

    suspend fun updateUserCredentials(key: String, username: String, password: String) {
        userDao.updateUserCredentials(key, username.trim(), password.trim())

        val updates = mapOf<String, Any>(
            "user" to username.trim(),
            "pass" to password.trim()
        )
        try {
            userRef.child(key).updateChildren(updates).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to update user in Firebase: ${e.message}", e)
        }
    }

    suspend fun toggleUserStatus(key: String, activate: Boolean) {
        val statusVal = if (activate) "true" else "false"
        userDao.toggleUserStatus(key, statusVal)

        try {
            userRef.child(key).child("status").setValue(statusVal).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to toggle user status in Firebase: ${e.message}", e)
        }
    }

    suspend fun deleteUser(key: String) {
        userDao.deleteByKey(key)

        try {
            userRef.child(key).removeValue().awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to delete user in Firebase: ${e.message}", e)
        }
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

        val map = hashMapOf<String, Any>(
            "user" to newSeller.user,
            "pass" to newSeller.pass,
            "status" to "true",
            "access" to newSeller.access,
            "key" to newSeller.key,
            "device" to "null",
            "version" to "null",
            "coin" to newSeller.coin
        )

        try {
            sellerRef.child(generatedKey).setValue(map).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to write seller to Firebase: ${e.message}", e)
        }

        return newSeller
    }

    suspend fun updateSellerCredentials(key: String, username: String, password: String) {
        sellerDao.updateSellerCredentials(key, username.trim(), password.trim())

        val updates = mapOf<String, Any>(
            "user" to username.trim(),
            "pass" to password.trim()
        )
        try {
            sellerRef.child(key).updateChildren(updates).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to update seller in Firebase: ${e.message}", e)
        }
    }

    suspend fun toggleSellerStatus(key: String, activate: Boolean) {
        val statusVal = if (activate) "true" else "false"
        sellerDao.toggleSellerStatus(key, statusVal)

        try {
            sellerRef.child(key).child("status").setValue(statusVal).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to toggle seller status in Firebase: ${e.message}", e)
        }
    }

    suspend fun deleteSeller(key: String) {
        sellerDao.deleteByKey(key)

        try {
            sellerRef.child(key).removeValue().awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to delete seller in Firebase: ${e.message}", e)
        }
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

        val map = hashMapOf<String, Any>(
            "version" to version.trim(),
            "message" to message.trim(),
            "link" to link.trim()
        )

        try {
            maintenanceRef.setValue(map).awaitTask()
            legacyUpRef.setValue(map).awaitTask()
        } catch (e: Exception) {
            Log.e("AdminRepository", "Failed to write maintenance to Firebase: ${e.message}", e)
        }
    }

    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
}
