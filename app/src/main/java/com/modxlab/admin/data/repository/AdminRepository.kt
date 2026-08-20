package com.modxlab.admin.data.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
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
    private val functions = Firebase.functions("asia-southeast1")
    
    private val userRef by lazy { firebaseDatabase.getReference("User") }
    private val sellerRef by lazy { firebaseDatabase.getReference("Seller") }
    private val maintenanceRef by lazy { firebaseDatabase.getReference("update/up") }
    private val legacyUpRef by lazy { firebaseDatabase.getReference("up") }
    private val keysRef by lazy { firebaseDatabase.getReference("Keys") }
    private val resellersRef by lazy { firebaseDatabase.getReference("Resellers") }

    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allSellers: Flow<List<SellerEntity>> = sellerDao.getAllSellers()
    val maintenance: Flow<MaintenanceEntity?> = maintenanceDao.getMaintenanceFlow()

    val dashboardStats: Flow<DashboardStats> = combine(
        allUsers,
        allSellers
    ) { users, sellers ->
        DashboardStats(
            totalUsers = users.size,
            activeUsers = users.count { it.status == "true" },
            inactiveUsers = users.count { it.status != "true" },
            totalSellers = sellers.size,
            activeSellers = sellers.count { it.status == "true" },
            inactiveSellers = sellers.count { it.status != "true" }
        )
    }

    init {
        setupFirebaseListeners()
    }

    private fun setupFirebaseListeners() {
        try {
            val userListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val userList = mutableListOf<UserEntity>()
                            for (child in snapshot.children) {
                                val key = child.child("key").getValue(String::class.java) ?: continue
                                val user = child.child("user").getValue(String::class.java) ?: continue
                                val pass = child.child("pass").getValue(String::class.java) ?: continue
                                val status = child.child("status").getValue(String::class.java) ?: "false"
                                val access = child.child("access").getValue(String::class.java) ?: "1"
                                val device = child.child("device").getValue(String::class.java) ?: "null"
                                val version = child.child("version").getValue(String::class.java) ?: "null"
                                val rgtime = child.child("rgtime").getValue(String::class.java) ?: ""
                                val time = child.child("time").getValue(String::class.java) ?: ""
                                val validity = child.child("Validity").getValue(String::class.java) ?: ""
                                userList.add(UserEntity(key, user, pass, status, access, device, version, rgtime, time, validity))
                            }
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
            }
            userRef.addValueEventListener(userListener)
            keysRef.addValueEventListener(userListener)

            val sellerListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val sellerList = mutableListOf<SellerEntity>()
                            for (child in snapshot.children) {
                                val key = child.key ?: continue
                                val user = child.child("user").getValue(String::class.java)
                                val username = child.child("username").getValue(String::class.java)
                                val finalUser = user ?: username ?: continue
                                
                                val pass = child.child("pass").getValue(String::class.java) ?: ""
                                val status = child.child("status").getValue(String::class.java) ?: "false"
                                val mappedStatus = if (status == "active") "true" else if (status == "inactive") "false" else status
                                val access = child.child("access").getValue(String::class.java) ?: "1"
                                val device = child.child("device").getValue(String::class.java) ?: "null"
                                val version = child.child("version").getValue(String::class.java) ?: "null"
                                val coinVal = child.child("coin").getValue(String::class.java)
                                val creditsVal = child.child("credits").getValue(Any::class.java)?.toString()
                                val finalCoin = coinVal ?: creditsVal ?: "0"

                                sellerList.add(SellerEntity(key, finalUser, pass, mappedStatus, access, device, version, finalCoin))
                            }
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
            }
            sellerRef.addValueEventListener(sellerListener)
            resellersRef.addValueEventListener(sellerListener)

            val maintenanceListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val version = snapshot.child("version").getValue(String::class.java) ?: "1.0.0"
                            val message = snapshot.child("message").getValue(String::class.java) ?: ""
                            val link = snapshot.child("link").getValue(String::class.java) ?: ""
                            scope.launch(Dispatchers.IO) {
                                maintenanceDao.insertOrUpdate(MaintenanceEntity(id = "up", version = version, message = message, link = link, updatedAt = System.currentTimeMillis()))
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

    suspend fun getUserByKey(key: String): UserEntity? = userDao.getUserByKey(key)

    suspend fun addUser(username: String, password: String, access: String, validityHours: Double): UserEntity {
        val data = hashMapOf(
            "user" to username.trim(),
            "pass" to password.trim(),
            "access" to access,
            "validityHours" to validityHours
        )
        val result = functions.getHttpsCallable("addUser").call(data).awaitTask()
        val resultData = result.getData() as Map<String, Any>
        val key = resultData["key"] as String
        
        val now = System.currentTimeMillis()
        val expiryMillis = now + (validityHours * 3600_000.0).toLong()
        val newUser = UserEntity(
            key = key,
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
        val data = hashMapOf("key" to key, "user" to username.trim(), "pass" to password.trim())
        try {
            functions.getHttpsCallable("updateUserCredentials").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
    }

    suspend fun toggleUserStatus(key: String, activate: Boolean) {
        val statusVal = if (activate) "true" else "false"
        userDao.toggleUserStatus(key, statusVal)
        val data = hashMapOf("key" to key, "status" to statusVal)
        try {
            functions.getHttpsCallable("toggleUserStatus").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
    }

    suspend fun deleteUser(key: String) {
        userDao.deleteByKey(key)
        val data = hashMapOf("key" to key)
        try {
            functions.getHttpsCallable("deleteUser").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
    }

    suspend fun getSellerByKey(key: String): SellerEntity? = sellerDao.getSellerByKey(key)

    suspend fun addSeller(username: String, password: String, access: String, coin: String): SellerEntity {
        val data = hashMapOf(
            "username" to username.trim(),
            "password" to password.trim(),
            "access" to access,
            "coin" to coin.trim()
        )
        val result = functions.getHttpsCallable("createReseller").call(data).awaitTask()
        val resultData = result.getData() as Map<String, Any>
        val uid = resultData["uid"] as String
        
        val newSeller = SellerEntity(
            key = uid,
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
        val data = hashMapOf("key" to key, "user" to username.trim(), "pass" to password.trim())
        try {
            functions.getHttpsCallable("updateSellerCredentials").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
    }

    suspend fun toggleSellerStatus(key: String, activate: Boolean) {
        val statusVal = if (activate) "true" else "false"
        sellerDao.toggleSellerStatus(key, statusVal)
        val data = hashMapOf("key" to key, "status" to statusVal)
        try {
            functions.getHttpsCallable("toggleSellerStatus").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
    }

    suspend fun deleteSeller(key: String) {
        sellerDao.deleteByKey(key)
        val data = hashMapOf("key" to key)
        try {
            functions.getHttpsCallable("deleteSeller").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
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
        val data = hashMapOf("version" to version.trim(), "message" to message.trim(), "link" to link.trim())
        try {
            functions.getHttpsCallable("setMaintenanceUpdate").call(data).awaitTask()
        } catch (e: Exception) { Log.e("AdminRepo", e.message.toString()) }
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
