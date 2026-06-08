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
    private val debtDao: DebtDao,
    private val firestoreService: com.app.walletcek.data.network.FirestoreService = com.app.walletcek.data.network.FirestoreService()
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()

    suspend fun getDebtById(id: Int): DebtEntity? = debtDao.getDebtById(id)

    suspend fun insertDebt(debt: DebtEntity) {
        debtDao.insertDebt(debt)
        firestoreService.syncDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
        firestoreService.syncDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
        firestoreService.deleteDebt(debt.id)
    }

    suspend fun deleteAllDebts() {
        debtDao.deleteAllDebts()
        // Firestore bulk delete can be added if needed
    }

    suspend fun deletePaidDebts() {
        debtDao.deletePaidDebts()
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        firestoreService.syncTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
        firestoreService.deleteTransaction(transaction.id)
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
        firestoreService.syncCategory(category)
    }

    suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
        categories.forEach { firestoreService.syncCategory(it) }
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
        firestoreService.deleteCategory(category.id)
    }

    suspend fun getCategoryCount(): Int = categoryDao.getCategoryCount()

    suspend fun syncFromCloud() {
        // Fetch from Firestore
        val cloudTransactions = firestoreService.fetchTransactions()
        val cloudDebts = firestoreService.fetchDebts()
        val cloudCategories = firestoreService.fetchCategories()

        // Insert into Local Room (using replace on conflict)
        cloudTransactions.forEach { transactionDao.insertTransaction(it) }
        cloudDebts.forEach { debtDao.insertDebt(it) }
        cloudCategories.forEach { categoryDao.insertCategory(it) }
    }
}
