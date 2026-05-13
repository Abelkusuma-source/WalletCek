package com.app.walletcek.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.walletcek.data.dao.CategoryDao
import com.app.walletcek.data.dao.TransactionDao
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.utils.Converters

@Database(entities = [CategoryEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wallet_cek_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
