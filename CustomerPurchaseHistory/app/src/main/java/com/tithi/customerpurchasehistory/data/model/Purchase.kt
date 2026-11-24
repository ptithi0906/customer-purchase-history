package com.tithi.customerpurchasehistory.data.model

import com.google.firebase.Timestamp

/**
 * Firestore document model for collection: "purchases"
 * Linked with customerId from the "customers" collection.
 */
data class Purchase(
    val purchaseId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val productName: String = "",
    val category: String = "",
    val quantity: Int = 1,
    val pricePerUnit: Double = 0.0,
    val totalAmount: Double = 0.0,
    val purchaseDate: String = "",
    val paymentMethod: String = "UPI",
    val createdAt: Timestamp = Timestamp.now()
)
