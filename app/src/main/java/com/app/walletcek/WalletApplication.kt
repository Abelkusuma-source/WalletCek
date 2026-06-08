package com.app.walletcek

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.app.walletcek.data.database.AppDatabase
import com.app.walletcek.data.repository.WalletRepository
import com.app.walletcek.worker.SyncWorker
import java.util.concurrent.TimeUnit

class WalletApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WalletRepository(database.categoryDao(), database.transactionDao(), database.debtDao()) }
    val authRepository by lazy { com.app.walletcek.data.repository.AuthRepository() }

    override fun onCreate() {
        super.onCreate()
        setupPeriodicSync()
    }

    private fun setupPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CloudSync",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
