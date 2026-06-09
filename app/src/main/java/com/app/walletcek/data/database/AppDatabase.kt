package com.app.walletcek.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.walletcek.data.dao.CategoryDao
import com.app.walletcek.data.dao.DebtDao
import com.app.walletcek.data.dao.TransactionDao
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.utils.Converters

@Database(entities = [CategoryEntity::class, TransactionEntity::class, DebtEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS transactions_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        amount REAL NOT NULL,
                        note TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        categoryId TEXT NOT NULL
                    )
                """.trimIndent())
                db.execSQL("INSERT INTO transactions_new (id, amount, note, date, type, categoryId) SELECT CAST(id AS TEXT), amount, note, date, type, CAST(categoryId AS TEXT) FROM transactions")
                db.execSQL("DROP TABLE transactions")
                db.execSQL("ALTER TABLE transactions_new RENAME TO transactions")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS categories_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        isDefault INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("INSERT INTO categories_new (id, name, type, isDefault) SELECT CAST(id AS TEXT), name, type, isDefault FROM categories")
                db.execSQL("DROP TABLE categories")
                db.execSQL("ALTER TABLE categories_new RENAME TO categories")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS debts_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        personName TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount REAL NOT NULL,
                        paidAmount REAL NOT NULL,
                        startDate INTEGER NOT NULL,
                        dueDate INTEGER,
                        note TEXT NOT NULL,
                        status TEXT NOT NULL
                    )
                """.trimIndent())
                db.execSQL("INSERT INTO debts_new (id, personName, type, amount, paidAmount, startDate, dueDate, note, status) SELECT CAST(id AS TEXT), personName, type, amount, paidAmount, startDate, dueDate, note, status FROM debts")
                db.execSQL("DROP TABLE debts")
                db.execSQL("ALTER TABLE debts_new RENAME TO debts")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wallet_cek_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
