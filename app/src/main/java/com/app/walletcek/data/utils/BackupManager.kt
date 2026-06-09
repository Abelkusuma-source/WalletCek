package com.app.walletcek.data.utils

import android.content.Context
import android.util.Log
import com.app.walletcek.data.entity.CategoryEntity
import com.app.walletcek.data.entity.DebtEntity
import com.app.walletcek.data.entity.TransactionEntity
import com.app.walletcek.data.model.DebtStatus
import com.app.walletcek.data.model.DebtType
import com.app.walletcek.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class BackupManager(private val context: Context) {

    private val backupDir: File get() = File(context.filesDir, "backups").also { it.mkdirs() }
    private val backupFile: File get() = File(backupDir, "wallet_data.json")

    suspend fun saveBackup(
        transactions: List<TransactionEntity>,
        debts: List<DebtEntity>,
        categories: List<CategoryEntity>
    ) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("version", 2)
                put("transactions", JSONArray().apply {
                    transactions.forEach { t -> put(transactionToJson(t)) }
                })
                put("debts", JSONArray().apply {
                    debts.forEach { d -> put(debtToJson(d)) }
                })
                put("categories", JSONArray().apply {
                    categories.forEach { c -> put(categoryToJson(c)) }
                })
            }
            backupFile.writeText(json.toString(2))
        } catch (e: Exception) {
            Log.e("BackupManager", "saveBackup failed", e)
        }
    }

    suspend fun restoreIfNeeded(
        transactionCount: Int,
        debtCount: Int,
        categoryCount: Int
    ): Boolean = withContext(Dispatchers.IO) {
        if (!backupFile.exists()) return@withContext false
        if (transactionCount > 0 || debtCount > 0 || categoryCount > 0) return@withContext false

        return@withContext try {
            val json = JSONObject(backupFile.readText())

            val restoredTransactions = json.optJSONArray("transactions")?.let { arr ->
                (0 until arr.length()).mapNotNull { i -> jsonToTransaction(arr.getJSONObject(i)) }
            } ?: emptyList()

            val restoredDebts = json.optJSONArray("debts")?.let { arr ->
                (0 until arr.length()).mapNotNull { i -> jsonToDebt(arr.getJSONObject(i)) }
            } ?: emptyList()

            val restoredCategories = json.optJSONArray("categories")?.let { arr ->
                (0 until arr.length()).mapNotNull { i -> jsonToCategory(arr.getJSONObject(i)) }
            } ?: emptyList()

            RestoreData(restoredTransactions, restoredDebts, restoredCategories)
        } catch (e: Exception) {
            Log.e("BackupManager", "restore failed", e)
            null
        }?.let { data ->
            backupFile.delete()
            true
        } ?: false
    }

    data class RestoreData(
        val transactions: List<TransactionEntity>,
        val debts: List<DebtEntity>,
        val categories: List<CategoryEntity>
    )

    private fun transactionToJson(t: TransactionEntity) = JSONObject().apply {
        put("id", t.id)
        put("amount", t.amount)
        put("note", t.note)
        put("date", t.date)
        put("type", t.type.name)
        put("categoryId", t.categoryId)
    }

    private fun debtToJson(d: DebtEntity) = JSONObject().apply {
        put("id", d.id)
        put("personName", d.personName)
        put("type", d.type.name)
        put("amount", d.amount)
        put("paidAmount", d.paidAmount)
        put("startDate", d.startDate)
        put("dueDate", d.dueDate ?: JSONObject.NULL)
        put("note", d.note)
        put("status", d.status.name)
    }

    private fun categoryToJson(c: CategoryEntity) = JSONObject().apply {
        put("id", c.id)
        put("name", c.name)
        put("type", c.type.name)
        put("isDefault", c.isDefault)
    }

    private fun jsonToTransaction(json: JSONObject) = try {
        TransactionEntity(
            id = json.getString("id"),
            amount = json.getDouble("amount"),
            note = json.optString("note", ""),
            date = json.getLong("date"),
            type = try { TransactionType.valueOf(json.getString("type")) } catch (_: Exception) { TransactionType.EXPENSE },
            categoryId = json.optString("categoryId", "")
        )
    } catch (e: Exception) {
        Log.w("BackupManager", "invalid transaction in backup", e)
        null
    }

    private fun jsonToDebt(json: JSONObject) = try {
        DebtEntity(
            id = json.getString("id"),
            personName = json.optString("personName", ""),
            type = try { DebtType.valueOf(json.getString("type")) } catch (_: Exception) { DebtType.DEBT },
            amount = json.getDouble("amount"),
            paidAmount = json.optDouble("paidAmount", 0.0),
            startDate = json.getLong("startDate"),
            dueDate = if (json.isNull("dueDate")) null else json.optLong("dueDate"),
            note = json.optString("note", ""),
            status = try { DebtStatus.valueOf(json.getString("status")) } catch (_: Exception) { DebtStatus.OPEN }
        )
    } catch (e: Exception) {
        Log.w("BackupManager", "invalid debt in backup", e)
        null
    }

    private fun jsonToCategory(json: JSONObject) = try {
        CategoryEntity(
            id = json.getString("id"),
            name = json.optString("name", ""),
            type = try { TransactionType.valueOf(json.getString("type")) } catch (_: Exception) { TransactionType.EXPENSE },
            isDefault = json.optBoolean("isDefault", false)
        )
    } catch (e: Exception) {
        Log.w("BackupManager", "invalid category in backup", e)
        null
    }

    fun hasBackup(): Boolean = backupFile.exists()
}
