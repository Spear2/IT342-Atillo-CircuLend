package com.atillo.circulend.data.model.response

data class BorrowResponse(
    val transactionId: Long,
    val itemId: Long,
    val itemName: String,
    val borrowDate: String,
    val status: String
)
