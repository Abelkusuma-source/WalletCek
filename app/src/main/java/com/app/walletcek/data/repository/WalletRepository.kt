package com.app.walletcek.data.repository

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
    private val debtDao: DebtDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()

    suspend fun getDebtById(id: Int): DebtEntity? = debtDao.getDebtById(id)

    suspend fun insertDebt(debt: DebtEntity) {
        debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
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
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun getCategoryCount(): Int = categoryDao.getCategoryCount()
}
