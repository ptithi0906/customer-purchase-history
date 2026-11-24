package com.tithi.customerpurchasehistory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tithi.customerpurchasehistory.data.model.Customer
import com.tithi.customerpurchasehistory.data.repository.FirestoreRepository
import com.tithi.customerpurchasehistory.ui.theme.DarkBluePrimary
import com.tithi.customerpurchasehistory.ui.theme.ErrorCrimson
import com.tithi.customerpurchasehistory.ui.theme.LightBlueSurface
import com.tithi.customerpurchasehistory.ui.theme.OffWhiteBackground
import com.tithi.customerpurchasehistory.ui.theme.PureWhite
import com.tithi.customerpurchasehistory.ui.theme.RoyalBlueAccent
import com.tithi.customerpurchasehistory.ui.theme.SlateText
import com.tithi.customerpurchasehistory.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

@Composable
fun CustomerListScreen(
    firestoreRepository: FirestoreRepository,
    onEditCustomer: (Customer) -> Unit,
    onViewCustomerPurchases: (Customer) -> Unit
) {
    val scope = rememberCoroutineScope()
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    var selectedCustomerForDetails by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    fun refreshCustomers() {
        isLoading = true
        scope.launch {
            val res = firestoreRepository.getAllCustomers()
            isLoading = false
            if (res.isSuccess) {
                customers = res.getOrDefault(emptyList())
                isError = false
            } else {
                isError = true
                statusMessage = res.exceptionOrNull()?.message
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshCustomers()
    }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                it.customerId.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Action Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search customer by name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { refreshCustomers() },
                modifier = Modifier
                    .size(52.dp)
                    .background(DarkBluePrimary, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh List", tint = PureWhite)
            }
        }

        // Seed Dummy Customers button
        OutlinedButton(
            onClick = {
                scope.launch {
                    val res = firestoreRepository.seedDummyDataIfRequested(onlyIfEmpty = false)
                    if (res.isSuccess) {
                        isError = false
                        statusMessage = "Loaded ${res.getOrDefault(0)} sample customers into Firestore."
                        refreshCustomers()
                    } else {
                        isError = true
                        statusMessage = res.exceptionOrNull()?.message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = DarkBluePrimary)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Seed 10 Sample Customers (Rahul, Priya, Amit...)", color = DarkBluePrimary)
        }

        statusMessage?.let { msg ->
            Text(
                text = msg,
                color = if (isError) ErrorCrimson else SuccessGreen,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkBluePrimary)
            }
        } else if (filteredCustomers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No matching customer records found.",
                    color = SlateText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCustomers, key = { it.customerId }) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCustomerForDetails = customer },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = customer.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBluePrimary
                                    )
                                    Text(
                                        text = "${customer.customerId} · ${customer.phoneNo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateText
                                    )
                                }
                                Row {
                                    IconButton(onClick = { onViewCustomerPurchases(customer) }) {
                                        Icon(
                                            Icons.Default.History,
                                            contentDescription = "Purchase History",
                                            tint = RoyalBlueAccent
                                        )
                                    }
                                    IconButton(onClick = { onEditCustomer(customer) }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Customer",
                                            tint = DarkBluePrimary
                                        )
                                    }
                                    IconButton(onClick = { customerToDelete = customer }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Customer",
                                            tint = ErrorCrimson
                                        )
                                    }
                                }
                            }
                            Text(
                                text = customer.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalBlueAccent
                            )
                            Text(
                                text = "DOB: ${customer.dob} · ${customer.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateText
                            )
                        }
                    }
                }
            }
        }
    }

    // Complete Customer Details Dialog
    selectedCustomerForDetails?.let { customer ->
        AlertDialog(
            onDismissRequest = { selectedCustomerForDetails = null },
            title = { Text(customer.name, fontWeight = FontWeight.Bold, color = DarkBluePrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailLine("Customer ID", customer.customerId)
                    DetailLine("Phone Number", customer.phoneNo)
                    DetailLine("Date of Birth", customer.dob)
                    DetailLine("Email", customer.email)
                    DetailLine("Address", customer.address)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedCustomerForDetails = null
                        onViewCustomerPurchases(customer)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBluePrimary)
                ) {
                    Text("View Purchases")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCustomerForDetails = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    customerToDelete?.let { customer ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Delete Customer Record?") },
            text = {
                Text("Are you sure you want to permanently delete ${customer.name} (${customer.customerId}) from Cloud Firestore?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val res = firestoreRepository.deleteCustomer(customer.customerId)
                            customerToDelete = null
                            if (res.isSuccess) {
                                isError = false
                                statusMessage = "Deleted ${customer.name} (${customer.customerId})."
                                refreshCustomers()
                            } else {
                                isError = true
                                statusMessage = res.exceptionOrNull()?.message
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBlueSurface, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = SlateText)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = DarkBluePrimary
        )
    }
}
