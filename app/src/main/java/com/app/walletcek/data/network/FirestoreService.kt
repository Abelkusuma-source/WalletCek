package com.app.walletcek.data.network

import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val userId: String? get() = auth.currentUser?.uid

    private fun getUserDoc() = userId?.let { firestore.collection("users").document(it) }

    suspend fun syncTransaction(transaction: TransactionEntity) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("transactions").document(transaction.id.toString())
            .set(transaction, SetOptions.merge()).await()
    }

    suspend fun deleteTransaction(transactionId: Int) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("transactions").document(transactionId.toString()).delete().await()
    }

    suspend fun syncDebt(debt: DebtEntity) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("debts").document(debt.id.toString())
            .set(debt, SetOptions.merge()).await()
    }

    suspend fun deleteDebt(debtId: Int) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("debts").document(debtId.toString()).delete().await()
    }

    suspend fun syncCategory(category: CategoryEntity) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("categories").document(category.id.toString())
            .set(category, SetOptions.merge()).await()
    }

    suspend fun deleteCategory(categoryId: Int) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("categories").document(categoryId.toString()).delete().await()
    }
    
    suspend fun fetchTransactions(): List<TransactionEntity> {
        val userDoc = getUserDoc() ?: return emptyList()
        return userDoc.collection("transactions").get().await().toObjects(TransactionEntity::class.java)
    }

    suspend fun fetchDebts(): List<DebtEntity> {
        val userDoc = getUserDoc() ?: return emptyList()
        return userDoc.collection("debts").get().await().toObjects(DebtEntity::class.java)
    }

    suspend fun fetchCategories(): List<CategoryEntity> {
        val userDoc = getUserDoc() ?: return emptyList()
        return userDoc.collection("categories").get().await().toObjects(CategoryEntity::class.java)
    }

    suspend fun deleteAllData() {
        // Implementation for bulk deletion if needed
    }
}
