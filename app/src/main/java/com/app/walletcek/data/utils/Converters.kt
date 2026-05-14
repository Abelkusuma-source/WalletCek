package com.app.walletcek.data.utils

import androidx.room.TypeConverter
import com.app.walletcek.data.model.DebtStatus
import com.app.walletcek.data.model.DebtType
import com.app.walletcek.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromDebtType(value: DebtType): String {
        return value.name
    }

    @TypeConverter
    fun toDebtType(value: String): DebtType {
        return DebtType.valueOf(value)
    }

    @TypeConverter
    fun fromDebtStatus(value: DebtStatus): String {
        return value.name
    }

    @TypeConverter
    fun toDebtStatus(value: String): DebtStatus {
        return DebtStatus.valueOf(value)
    }
}
