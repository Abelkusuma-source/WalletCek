package com.app.walletcek.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.walletcek.data.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: TransactionType,
    val isDefault: Boolean = false
)
