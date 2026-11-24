package com.tithi.customerpurchasehistory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.tithi.customerpurchasehistory.data.DummyDataSeeder
import com.tithi.customerpurchasehistory.data.model.Customer
import com.tithi.customerpurchasehistory.data.model.Purchase
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FirestoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val customersCollection = db.collection("customers")
    private val purchasesCollection = db.collection("purchases")

    suspend fun getAllCustomers(): Result<List<Customer>> =
        suspendCancellableCoroutine { cont ->
            customersCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull { it.toObject(Customer::class.java) }
                    cont.resume(Result.success(list))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                }
        }

    suspend fun generateNextCustomerId(): String {
        val existing = getAllCustomers().getOrDefault(emptyList())
        val maxNumber = existing
            .mapNotNull { it.customerId.removePrefix("CUST-").toIntOrNull() }
            .maxOrNull() ?: 1000
        return "CUST-${maxNumber + 1}"
    }

    suspend fun generateNextPurchaseId(): String {
        val existing = getAllPurchases().getOrDefault(emptyList())
        val maxNumber = existing
            .mapNotNull { it.purchaseId.removePrefix("PUR-").toIntOrNull() }
            .maxOrNull() ?: 5000
        return "PUR-${maxNumber + 1}"
    }

    suspend fun saveCustomer(customer: Customer): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            // Prevent duplicate email or phone number records
            customersCollection.get()
                .addOnSuccessListener { snapshot ->
                    val existing = snapshot.documents.mapNotNull { it.toObject(Customer::class.java) }
                    val duplicateEmail = existing.any {
                        it.email.equals(customer.email.trim(), ignoreCase = true) &&
                            it.customerId != customer.customerId
                    }
                    val duplicatePhone = existing.any {
                        it.phoneNo.trim() == customer.phoneNo.trim() &&
                            it.customerId != customer.customerId
                    }
                    val duplicateId = existing.any { it.customerId == customer.customerId }

                    when {
                        duplicateId -> cont.resume(Result.failure(Exception("Customer ID ${customer.customerId} already exists.")))
                        duplicateEmail -> cont.resume(Result.failure(Exception("A customer with email ${customer.email} already exists.")))
                        duplicatePhone -> cont.resume(Result.failure(Exception("A customer with phone ${customer.phoneNo} already exists.")))
                        else -> {
                            val cleanCustomer = customer.copy(
                                name = customer.name.trim(),
                                phoneNo = customer.phoneNo.trim(),
                                dob = customer.dob.trim(),
                                email = customer.email.trim(),
                                address = customer.address.trim(),
                                createdAt = Timestamp.now()
                            )
                            customersCollection.document(cleanCustomer.customerId)
                                .set(cleanCustomer)
                                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                                .addOnFailureListener { e ->
                                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                }
        }

    suspend fun updateCustomer(customer: Customer): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            customersCollection.get()
                .addOnSuccessListener { snapshot ->
                    val existing = snapshot.documents.mapNotNull { it.toObject(Customer::class.java) }
                    val duplicateEmail = existing.any {
                        it.email.equals(customer.email.trim(), ignoreCase = true) &&
                            it.customerId != customer.customerId
                    }
                    val duplicatePhone = existing.any {
                        it.phoneNo.trim() == customer.phoneNo.trim() &&
                            it.customerId != customer.customerId
                    }

                    when {
                        duplicateEmail -> cont.resume(Result.failure(Exception("Another customer already uses email ${customer.email}.")))
                        duplicatePhone -> cont.resume(Result.failure(Exception("Another customer already uses phone ${customer.phoneNo}.")))
                        else -> {
                            val updates = mapOf(
                                "name" to customer.name.trim(),
                                "phoneNo" to customer.phoneNo.trim(),
                                "dob" to customer.dob.trim(),
                                "email" to customer.email.trim(),
                                "address" to customer.address.trim()
                            )
                            customersCollection.document(customer.customerId)
                                .update(updates)
                                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                                .addOnFailureListener { e ->
                                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                }
        }

    suspend fun deleteCustomer(customerId: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            customersCollection.document(customerId)
                .delete()
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { e -> cont.resume(Result.failure(Exception(mapFirestoreError(e)))) }
        }

    suspend fun getAllPurchases(): Result<List<Purchase>> =
        suspendCancellableCoroutine { cont ->
            purchasesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull { it.toObject(Purchase::class.java) }
                    cont.resume(Result.success(list))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                }
        }

    suspend fun getPurchasesForCustomer(customerId: String): Result<List<Purchase>> =
        suspendCancellableCoroutine { cont ->
            purchasesCollection
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents
                        .mapNotNull { it.toObject(Purchase::class.java) }
                        .sortedByDescending { it.createdAt.seconds }
                    cont.resume(Result.success(list))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                }
        }

    suspend fun addPurchase(purchase: Purchase): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val cleanPurchase = purchase.copy(
                productName = purchase.productName.trim(),
                totalAmount = purchase.quantity * purchase.pricePerUnit,
                createdAt = Timestamp.now()
            )
            purchasesCollection.document(cleanPurchase.purchaseId)
                .set(cleanPurchase)
                .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                .addOnFailureListener { e -> cont.resume(Result.failure(Exception(mapFirestoreError(e)))) }
        }

    /**
     * Seeds the 10 mandatory dummy customers and sample purchases into Firestore
     * without duplicating existing documents.
     */
    suspend fun seedDummyDataIfRequested(onlyIfEmpty: Boolean = false): Result<Int> =
        suspendCancellableCoroutine { cont ->
            customersCollection.get()
                .addOnSuccessListener { snapshot ->
                    if (onlyIfEmpty && !snapshot.isEmpty) {
                        cont.resume(Result.success(0))
                        return@addOnSuccessListener
                    }
                    val existingIds = snapshot.documents.map { it.id }.toSet()
                    val batch = db.batch()
                    var addedCount = 0

                    DummyDataSeeder.sampleCustomers.forEach { customer ->
                        if (!existingIds.contains(customer.customerId)) {
                            val ref = customersCollection.document(customer.customerId)
                            batch.set(ref, customer)
                            addedCount++
                        }
                    }

                    DummyDataSeeder.samplePurchases.forEach { purchase ->
                        val ref = purchasesCollection.document(purchase.purchaseId)
                        batch.set(ref, purchase)
                    }

                    batch.commit()
                        .addOnSuccessListener { cont.resume(Result.success(addedCount)) }
                        .addOnFailureListener { e ->
                            cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                        }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(Exception(mapFirestoreError(e))))
                }
        }

    private fun mapFirestoreError(exception: Exception): String {
        if (exception is FirebaseFirestoreException) {
            return when (exception.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "Permission denied by Firestore security rules. Please ensure you are logged in."
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "Cloud Firestore is currently unavailable. Check your internet connection."
                else -> exception.localizedMessage ?: "Firestore database error occurred."
            }
        }
        return exception.localizedMessage ?: "Database error occurred."
    }
}
