package com.app.walletcek.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.walletcek.WalletApplication

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as WalletApplication).repository
        return try {
            repository.syncFromCloud()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
