package com.app.walletcek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import com.app.walletcek.data.repository.WalletRepository
import com.app.walletcek.data.utils.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class WalletViewModel(
    private val repository: WalletRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val allCategories = repository.allCategories
    val allTransactions = repository.allTransactions
    val allDebts = repository.allDebts

    var themeMode = mutableStateOf(preferenceManager.getThemeMode())
        private set

    fun setThemeMode(mode: String) {
        themeMode.value = mode
        preferenceManager.setThemeMode(mode)
    }

    fun insertDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.insertDebt(debt)
            
            // Mencatat pengeluaran saat utang/piutang dibuat
            // Baik saya berutang (DEBT) maupun orang berutang (RECEIVABLE), 
            // dianggap sebagai "beban/pengeluaran" terhadap saldo bersih.
            repository.insertTransaction(
                TransactionEntity(
                    amount = debt.amount,
                    note = if (debt.type == com.app.walletcek.data.model.DebtType.RECEIVABLE) 
                        "Piutang ke ${debt.personName}" else "Hutang ke ${debt.personName}",
                    date = System.currentTimeMillis(),
                    type = TransactionType.EXPENSE,
                    categoryId = -1
                )
            )
        }
    }

    fun updateDebt(debt: DebtEntity, paymentAmount: Double) {
        viewModelScope.launch {
            repository.updateDebt(debt)
            
            // Setiap pembayaran (cicilan) akan menambah saldo (Income)
            // Ini akan mengimbangi pengeluaran yang dicatat saat utang dibuat.
            repository.insertTransaction(
                TransactionEntity(
                    amount = paymentAmount,
                    note = "Pembayaran Utang/Piutang: ${debt.personName}",
                    date = System.currentTimeMillis(),
                    type = TransactionType.INCOME,
                    categoryId = -1
                )
            )
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        repository.getCategoriesByType(type)

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            repository.deleteAllTransactions()
        }
    }

    init {
        checkAndInsertDefaultCategories()
        checkAndResetMonthlyData()
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
            // Jika baru pertama kali (fitur baru dipasang), catat bulan sekarang tanpa hapus data.
            // Penghapusan baru akan terjadi saat bulan berganti dari bulan ini ke bulan depan.
            preferenceManager.setLastResetMonth(currentMonth)
        } else if (lastResetMonth != currentMonth) {
            // Bulan telah berganti, hapus data yang sudah LUNAS dan Transaksi Recent
            viewModelScope.launch {
                repository.deletePaidDebts()
                repository.deleteAllTransactions()
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
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(repository, preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
