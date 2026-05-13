package com.app.walletcek

import android.app.Application
import com.app.walletcek.data.database.AppDatabase
import com.app.walletcek.data.repository.WalletRepository

class WalletApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WalletRepository(database.categoryDao(), database.transactionDao()) }
}
