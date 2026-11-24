package com.tithi.customerpurchasehistory.data.model

import com.google.firebase.Timestamp

/**
 * Firestore document model for collection: "customers"
 * Exact fields required by DBMS specification:
 * customerId, name, phoneNo, dob, email, address, createdAt
 */
data class Customer(
    val customerId: String = "",
    val name: String = "",
    val phoneNo: String = "",
    val dob: String = "",
    val email: String = "",
    val address: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
