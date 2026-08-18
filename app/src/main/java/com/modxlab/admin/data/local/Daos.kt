package com.modxlab.admin.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.modxlab.admin.data.model.MaintenanceEntity
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY time DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE `key` = :key LIMIT 1")
    suspend fun getUserByKey(key: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET user = :user, pass = :pass WHERE `key` = :key")
    suspend fun updateUserCredentials(key: String, user: String, pass: String)

    @Query("UPDATE users SET status = :status WHERE `key` = :key")
    suspend fun toggleUserStatus(key: String, status: String)

    @Query("UPDATE users SET device = 'null' WHERE `key` = :key")
    suspend fun resetHwid(key: String)

    @Query("UPDATE users SET time = :time, validity = :validity WHERE `key` = :key")
    suspend fun updateValidity(key: String, time: String, validity: String)

    @Query("DELETE FROM users WHERE `key` = :key")
    suspend fun deleteByKey(key: String)

    @Query("DELETE FROM users WHERE `key` NOT IN (:keys)")
    suspend fun deleteUsersNotIn(keys: List<String>)

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE status = 'true'")
    fun getActiveUserCount(): Flow<Int>
}

@Dao
interface SellerDao {
    @Query("SELECT * FROM sellers ORDER BY key DESC")
    fun getAllSellers(): Flow<List<SellerEntity>>

    @Query("SELECT * FROM sellers WHERE `key` = :key LIMIT 1")
    suspend fun getSellerByKey(key: String): SellerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeller(seller: SellerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSellers(sellers: List<SellerEntity>)

    @Update
    suspend fun updateSeller(seller: SellerEntity)

    @Query("UPDATE sellers SET user = :user, pass = :pass WHERE `key` = :key")
    suspend fun updateSellerCredentials(key: String, user: String, pass: String)

    @Query("UPDATE sellers SET status = :status WHERE `key` = :key")
    suspend fun toggleSellerStatus(key: String, status: String)

    @Query("UPDATE sellers SET device = 'null' WHERE `key` = :key")
    suspend fun resetHwid(key: String)

    @Query("UPDATE sellers SET coin = :coin WHERE `key` = :key")
    suspend fun updateCoin(key: String, coin: String)

    @Query("DELETE FROM sellers WHERE `key` = :key")
    suspend fun deleteByKey(key: String)

    @Query("DELETE FROM sellers WHERE `key` NOT IN (:keys)")
    suspend fun deleteSellersNotIn(keys: List<String>)

    @Query("SELECT COUNT(*) FROM sellers")
    fun getSellerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM sellers WHERE status = 'true'")
    fun getActiveSellerCount(): Flow<Int>
}

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance WHERE id = 'up' LIMIT 1")
    fun getMaintenanceFlow(): Flow<MaintenanceEntity?>

    @Query("SELECT * FROM maintenance WHERE id = 'up' LIMIT 1")
    suspend fun getMaintenance(): MaintenanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(maintenance: MaintenanceEntity)
}

