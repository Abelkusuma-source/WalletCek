package com.app.walletcek.data.network

import android.util.Log
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.DebtStatus
import com.app.walletcek.data.model.DebtType
import com.app.walletcek.data.model.TransactionType
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
        userDoc.collection("transactions").document(transaction.id)
            .set(transaction, SetOptions.merge()).await()
    }

    suspend fun deleteTransaction(transactionId: String) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("transactions").document(transactionId).delete().await()
    }

    suspend fun syncDebt(debt: DebtEntity) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("debts").document(debt.id)
            .set(debt, SetOptions.merge()).await()
    }

    suspend fun deleteDebt(debtId: String) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("debts").document(debtId).delete().await()
    }

    suspend fun syncCategory(category: CategoryEntity) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("categories").document(category.id)
            .set(category, SetOptions.merge()).await()
    }

    suspend fun deleteCategory(categoryId: String) {
        val userDoc = getUserDoc() ?: return
        userDoc.collection("categories").document(categoryId).delete().await()
    }

    suspend fun fetchTransactions(): List<TransactionEntity> {
        val userDoc = getUserDoc() ?: return emptyList()
        return try {
            userDoc.collection("transactions").get().await().documents.mapNotNull { doc ->
                try {
                    TransactionEntity(
                        id = doc.getString("id") ?: doc.getLong("id")?.toString() ?: return@mapNotNull null,
                        amount = doc.getDouble("amount") ?: 0.0,
                        note = doc.getString("note") ?: "",
                        date = doc.getLong("date") ?: System.currentTimeMillis(),
                        type = doc.getString("type")?.let { name ->
                            try { TransactionType.valueOf(name) } catch (_: Exception) { null }
                        } ?: TransactionType.EXPENSE,
                        categoryId = doc.getString("categoryId") ?: doc.getLong("categoryId")?.toString() ?: ""
                    )
                } catch (e: Exception) {
                    Log.w("FirestoreService", "Skip invalid transaction doc ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreService", "fetchTransactions failed", e)
            emptyList()
        }
    }

    suspend fun fetchDebts(): List<DebtEntity> {
        val userDoc = getUserDoc() ?: return emptyList()
        return try {
            userDoc.collection("debts").get().await().documents.mapNotNull { doc ->
                try {
                    DebtEntity(
                        id = doc.getString("id") ?: doc.getLong("id")?.toString() ?: return@mapNotNull null,
                        personName = doc.getString("personName") ?: "",
                        type = doc.getString("type")?.let { name ->
                            try { DebtType.valueOf(name) } catch (_: Exception) { null }
                        } ?: DebtType.DEBT,
                        amount = doc.getDouble("amount") ?: 0.0,
                        paidAmount = doc.getDouble("paidAmount") ?: 0.0,
                        startDate = doc.getLong("startDate") ?: System.currentTimeMillis(),
                        dueDate = if (doc.contains("dueDate")) doc.getLong("dueDate") else null,
                        note = doc.getString("note") ?: "",
                        status = doc.getString("status")?.let { name ->
                            try { DebtStatus.valueOf(name) } catch (_: Exception) { null }
                        } ?: DebtStatus.OPEN
                    )
                } catch (e: Exception) {
                    Log.w("FirestoreService", "Skip invalid debt doc ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreService", "fetchDebts failed", e)
            emptyList()
        }
    }

    suspend fun fetchCategories(): List<CategoryEntity> {
        val userDoc = getUserDoc() ?: return emptyList()
        return try {
            userDoc.collection("categories").get().await().documents.mapNotNull { doc ->
                try {
                    CategoryEntity(
                        id = doc.getString("id") ?: doc.getLong("id")?.toString() ?: return@mapNotNull null,
                        name = doc.getString("name") ?: "",
                        type = doc.getString("type")?.let { name ->
                            try { TransactionType.valueOf(name) } catch (_: Exception) { null }
                        } ?: TransactionType.EXPENSE,
                        isDefault = doc.getBoolean("isDefault") ?: false
                    )
                } catch (e: Exception) {
                    Log.w("FirestoreService", "Skip invalid category doc ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreService", "fetchCategories failed", e)
            emptyList()
        }
    }

    suspend fun deleteAllData() {
        // Implementation for bulk deletion if needed
    }
}
