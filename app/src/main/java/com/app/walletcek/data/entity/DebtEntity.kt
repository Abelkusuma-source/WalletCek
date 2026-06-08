package com.app.walletcek.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.walletcek.data.model.DebtStatus
import com.app.walletcek.data.model.DebtType

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val personName: String = "",
    val type: DebtType = DebtType.DEBT,
    val amount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val note: String = "",
    val status: DebtStatus = DebtStatus.OPEN
)
