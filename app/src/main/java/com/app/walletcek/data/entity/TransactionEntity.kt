package com.app.walletcek.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.walletcek.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double = 0.0,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Int = 0
)
