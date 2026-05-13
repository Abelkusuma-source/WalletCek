package com.app.walletcek.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.walletcek.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val note: String,
    val date: Long,
    val type: TransactionType,
    val categoryId: Int
)
