package com.app.walletcek.data.repository

import com.app.walletcek.data.dao.CategoryDao
import com.app.walletcek.data.dao.TransactionDao
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class WalletRepository(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

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
