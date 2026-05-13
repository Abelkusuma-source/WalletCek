package com.app.walletcek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import com.app.walletcek.data.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {

    val allCategories = repository.allCategories
    val allTransactions = repository.allTransactions

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

class WalletViewModelFactory(private val repository: WalletRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
