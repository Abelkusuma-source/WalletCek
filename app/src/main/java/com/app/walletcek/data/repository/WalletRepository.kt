package com.app.walletcek.data.repository

import android.util.Log
import com.app.walletcek.data.dao.CategoryDao
import com.app.walletcek.data.dao.DebtDao
import com.app.walletcek.data.dao.TransactionDao
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class WalletRepository(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val debtDao: DebtDao,
    private val firestoreService: com.app.walletcek.data.network.FirestoreService = com.app.walletcek.data.network.FirestoreService()
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()

    suspend fun getDebtById(id: String): DebtEntity? = debtDao.getDebtById(id)

    suspend fun insertDebt(debt: DebtEntity) {
        debtDao.insertDebt(debt)
        try {
            firestoreService.syncDebt(debt)
        } catch (e: Exception) {
            Log.e("WalletRepository", "syncDebt failed", e)
        }
    }

    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
        try {
            firestoreService.syncDebt(debt)
        } catch (e: Exception) {
            Log.e("WalletRepository", "syncDebt failed", e)
        }
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
        try {
            firestoreService.deleteDebt(debt.id)
        } catch (e: Exception) {
            Log.e("WalletRepository", "deleteDebt failed", e)
        }
    }

    suspend fun deleteAllDebts() {
        debtDao.deleteAllDebts()
    }

    suspend fun deletePaidDebts() {
        debtDao.deletePaidDebts()
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        try {
            firestoreService.syncTransaction(transaction)
        } catch (e: Exception) {
            Log.e("WalletRepository", "syncTransaction failed", e)
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
        try {
            firestoreService.deleteTransaction(transaction.id)
        } catch (e: Exception) {
            Log.e("WalletRepository", "deleteTransaction failed", e)
        }
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
        try {
            firestoreService.syncCategory(category)
        } catch (e: Exception) {
            Log.e("WalletRepository", "syncCategory failed", e)
        }
    }

    suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
        categories.forEach { category ->
            try {
                firestoreService.syncCategory(category)
            } catch (e: Exception) {
                Log.e("WalletRepository", "syncCategory failed", e)
            }
        }
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
        try {
            firestoreService.deleteCategory(category.id)
        } catch (e: Exception) {
            Log.e("WalletRepository", "deleteCategory failed", e)
        }
    }

    suspend fun getTransactionCount(): Int = transactionDao.getTransactionCount()
    suspend fun getDebtCount(): Int = debtDao.getDebtCount()
    suspend fun getCategoryCount(): Int = categoryDao.getCategoryCount()

    suspend fun syncFromCloud() {
        runCatching {
            firestoreService.fetchTransactions().forEach { transactionDao.insertTransaction(it) }
        }.onFailure { Log.e("WalletRepository", "syncFromCloud: transactions failed", it) }

        runCatching {
            firestoreService.fetchDebts().forEach { debtDao.insertDebt(it) }
        }.onFailure { Log.e("WalletRepository", "syncFromCloud: debts failed", it) }

        runCatching {
            firestoreService.fetchCategories().forEach { categoryDao.insertCategory(it) }
        }.onFailure { Log.e("WalletRepository", "syncFromCloud: categories failed", it) }
    }
}
