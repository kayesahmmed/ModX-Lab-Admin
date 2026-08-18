package com.modxlab.admin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.modxlab.admin.data.model.MaintenanceEntity
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [UserEntity::class, SellerEntity::class, MaintenanceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sellerDao(): SellerDao
    abstract fun maintenanceDao(): MaintenanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "modx_admin_db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val sellerDao = database.sellerDao()
            val maintenanceDao = database.maintenanceDao()

            val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.US)
            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60 * 60 * 1000

            // Seed Users
            val initialUsers = listOf(
                UserEntity(
                    key = "-NoxUser9821A",
                    user = "pro_gamer_alex",
                    pass = "ModX#99482",
                    status = "true",
                    access = "1",
                    device = "Xiaomi 13 Pro (2210132G)",
                    version = "v2.4.1-rc",
                    rgtime = sdf.format(Date(now - 3 * dayMillis)),
                    time = (now + 27 * dayMillis).toString(),
                    validity = sdf.format(Date(now + 27 * dayMillis))
                ),
                UserEntity(
                    key = "-NoxUser4412B",
                    user = "shadow_elite",
                    pass = "ElitePass*2026",
                    status = "true",
                    access = "∞",
                    device = "null",
                    version = "null",
                    rgtime = sdf.format(Date(now - 1 * dayMillis)),
                    time = (now + 14 * dayMillis).toString(),
                    validity = sdf.format(Date(now + 14 * dayMillis))
                ),
                UserEntity(
                    key = "-NoxUser1029C",
                    user = "cyber_knight",
                    pass = "KnightMod#77",
                    status = "false",
                    access = "1",
                    device = "Samsung Galaxy S24 Ultra",
                    version = "v2.3.9",
                    rgtime = sdf.format(Date(now - 10 * dayMillis)),
                    time = (now - 1 * dayMillis).toString(),
                    validity = sdf.format(Date(now - 1 * dayMillis))
                ),
                UserEntity(
                    key = "-NoxUser7734D",
                    user = "viper_strike",
                    pass = "VipKey#102",
                    status = "true",
                    access = "1",
                    device = "OnePlus 12",
                    version = "v2.4.0",
                    rgtime = sdf.format(Date(now - 2 * dayMillis)),
                    time = (now + 5 * dayMillis).toString(),
                    validity = sdf.format(Date(now + 5 * dayMillis))
                )
            )

            // Seed Sellers
            val initialSellers = listOf(
                SellerEntity(
                    key = "-NoxSeller001A",
                    user = "global_distributor",
                    pass = "Distributor#Alpha9",
                    status = "true",
                    access = "∞",
                    device = "null",
                    version = "null",
                    coin = "100"
                ),
                SellerEntity(
                    key = "-NoxSeller002B",
                    user = "reseller_asia_hub",
                    pass = "AsiaHub#Sec99",
                    status = "true",
                    access = "1",
                    device = "Google Pixel 8 Pro",
                    version = "v2.4.1",
                    coin = "75"
                ),
                SellerEntity(
                    key = "-NoxSeller003C",
                    user = "apex_partner",
                    pass = "Partner#ModX",
                    status = "false",
                    access = "1",
                    device = "Asus ROG Phone 8",
                    version = "v2.3.8",
                    coin = "25"
                )
            )

            // Seed Maintenance
            val initialMaintenance = MaintenanceEntity(
                id = "up",
                version = "2.4.2",
                message = "🔥 New ModX client update available with enhanced anti-detection, low-latency sync engine, and stability fixes.",
                link = "https://modxlab.app/downloads/modx-client-v2.4.2.apk",
                updatedAt = now
            )

            for (u in initialUsers) userDao.insertUser(u)
            for (s in initialSellers) sellerDao.insertSeller(s)
            maintenanceDao.insertOrUpdate(initialMaintenance)
        }
    }
}
