package com.app.walletcek.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import com.app.walletcek.data.repository.WalletRepository
import com.app.walletcek.data.utils.BackupManager
import com.app.walletcek.data.utils.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class WalletViewModel(
    private val repository: WalletRepository,
    private val preferenceManager: PreferenceManager,
    private val backupManager: BackupManager
) : ViewModel() {

    val allCategories = repository.allCategories
    val allTransactions = repository.allTransactions
    val allDebts = repository.allDebts

    var themeMode = mutableStateOf(preferenceManager.getThemeMode())
        private set

    var sharedText = mutableStateOf<String?>(null)
        private set

    fun clearSharedText() {
        sharedText.value = null
    }

    fun setThemeMode(mode: String) {
        themeMode.value = mode
        preferenceManager.setThemeMode(mode)
    }

    fun insertDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.insertDebt(debt)
            
            repository.insertTransaction(
                TransactionEntity(
                    amount = debt.amount,
                    note = if (debt.type == com.app.walletcek.data.model.DebtType.RECEIVABLE) 
                        "Piutang ke ${debt.personName}" else "Hutang ke ${debt.personName}",
                    date = System.currentTimeMillis(),
                    type = TransactionType.EXPENSE,
                    categoryId = "-1"
                )
            )
            saveBackup()
        }
    }

    fun updateDebt(debt: DebtEntity, paymentAmount: Double) {
        viewModelScope.launch {
            repository.updateDebt(debt)

            val lunasSuffix = if (debt.status == com.app.walletcek.data.model.DebtStatus.PAID) " (LUNAS)" else ""
            val prefix = if (debt.type == com.app.walletcek.data.model.DebtType.RECEIVABLE) "Pembayaran Piutang" else "Pembayaran Hutang"
            
            repository.insertTransaction(
                TransactionEntity(
                    amount = paymentAmount,
                    note = "$prefix: ${debt.personName}$lunasSuffix",
                    date = System.currentTimeMillis(),
                    type = TransactionType.INCOME,
                    categoryId = "-1"
                )
            )
            saveBackup()
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
            saveBackup()
        }
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        repository.getCategoriesByType(type)

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
            saveBackup()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            saveBackup()
        }
    }

    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
            saveBackup()
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            saveBackup()
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            repository.deleteAllTransactions()
            saveBackup()
        }
    }

    fun deleteAllDebts() {
        viewModelScope.launch {
            repository.deleteAllDebts()
            saveBackup()
        }
    }

    private suspend fun saveBackup() {
        try {
            backupManager.saveBackup(
                transactions = allTransactions.first(),
                debts = allDebts.first(),
                categories = allCategories.first()
            )
        } catch (e: Exception) {
            Log.e("WalletViewModel", "saveBackup failed", e)
        }
    }

    suspend fun syncFromCloud() {
        repository.syncFromCloud()
    }

    init {
        restoreFromBackup()
        checkAndInsertDefaultCategories()
        checkAndResetMonthlyData()
    }

    private fun restoreFromBackup() {
        viewModelScope.launch {
            val restored = backupManager.restoreIfNeeded(
                transactionCount = repository.getTransactionCount(),
                debtCount = repository.getDebtCount(),
                categoryCount = repository.getCategoryCount()
            )
            if (restored) {
                Log.i("WalletViewModel", "Data restored from local backup")
            }
        }
    }

    private fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllTransactions()
            repository.deleteAllDebts()
            preferenceManager.setLastResetMonth(-1)
        }
    }

    private fun checkAndResetMonthlyData() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val lastResetMonth = preferenceManager.getLastResetMonth()

        if (lastResetMonth == -1) {
            preferenceManager.setLastResetMonth(currentMonth)
        } else if (lastResetMonth != currentMonth) {
            // Bulan telah berganti — hanya hapus utang yang sudah lunas, data transaksi tetap dipertahankan
            viewModelScope.launch {
                repository.deletePaidDebts()
                preferenceManager.setLastResetMonth(currentMonth)
            }
        }
    }

    private fun checkAndInsertDefaultCategories() {
        viewModelScope.launch {
            val count = repository.getCategoryCount()
            if (count == 0) {
                val defaultCategories = listOf(
                    // Expenses
                    CategoryEntity(name = "Makanan", type = TransactionType.EXPENSE, isDefault = true),
                    CategoryEntity(name = "Transportasi", type = TransactionType.EXPENSE, isDefault = true),
                    CategoryEntity(name = "Belanja", type = TransactionType.EXPENSE, isDefault = true),
                    CategoryEntity(name = "Hiburan", type = TransactionType.EXPENSE, isDefault = true),
                    CategoryEntity(name = "Tagihan", type = TransactionType.EXPENSE, isDefault = true),
                    
                    // Income
                    CategoryEntity(name = "Gaji", type = TransactionType.INCOME, isDefault = true),
                    CategoryEntity(name = "Bonus", type = TransactionType.INCOME, isDefault = true),
                    CategoryEntity(name = "Investasi", type = TransactionType.INCOME, isDefault = true)
                )
                repository.insertCategories(defaultCategories)
            }
        }
    }
}

class WalletViewModelFactory(
    private val repository: WalletRepository,
    private val preferenceManager: PreferenceManager,
    private val backupManager: BackupManager
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(repository, preferenceManager, backupManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
