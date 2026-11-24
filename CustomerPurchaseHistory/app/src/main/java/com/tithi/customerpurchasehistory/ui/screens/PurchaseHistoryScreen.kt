package com.tithi.customerpurchasehistory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tithi.customerpurchasehistory.data.model.Customer
import com.tithi.customerpurchasehistory.data.model.Purchase
import com.tithi.customerpurchasehistory.data.repository.FirestoreRepository
import com.tithi.customerpurchasehistory.ui.theme.DarkBluePrimary
import com.tithi.customerpurchasehistory.ui.theme.ErrorCrimson
import com.tithi.customerpurchasehistory.ui.theme.LightBlueSurface
import com.tithi.customerpurchasehistory.ui.theme.OffWhiteBackground
import com.tithi.customerpurchasehistory.ui.theme.PureWhite
import com.tithi.customerpurchasehistory.ui.theme.RoyalBlueAccent
import com.tithi.customerpurchasehistory.ui.theme.SlateText
import com.tithi.customerpurchasehistory.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseHistoryScreen(
    firestoreRepository: FirestoreRepository,
    preselectedCustomerId: String?
) {
    val scope = rememberCoroutineScope()
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var customerPurchases by remember { mutableStateOf<List<Purchase>>(emptyList()) }

    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var quantityText by remember { mutableStateOf("1") }
    var pricePerUnitText by remember { mutableStateOf("") }
    var purchaseDate by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    }
    var paymentMethod by remember { mutableStateOf("UPI") }

    var customerDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Electronics", "Groceries", "Fashion", "Home & Kitchen",
        "Footwear", "Stationery", "Appliances", "Personal Care"
    )
    val paymentMethods = listOf("UPI", "Credit Card", "Debit Card", "Cash", "Net Banking")
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    val datePickerState = rememberDatePickerState()

    val quantity = quantityText.toIntOrNull() ?: 0
    val unitPrice = pricePerUnitText.toDoubleOrNull() ?: 0.0
    val totalAmount = quantity * unitPrice

    fun loadCustomerHistory(custId: String) {
        scope.launch {
            val res = firestoreRepository.getPurchasesForCustomer(custId)
            customerPurchases = res.getOrDefault(emptyList())
        }
    }

    LaunchedEffect(preselectedCustomerId) {
        val loaded = firestoreRepository.getAllCustomers().getOrDefault(emptyList())
        customers = loaded
        val initial = loaded.find { it.customerId == preselectedCustomerId } ?: loaded.firstOrNull()
        selectedCustomer = initial
        initial?.let { loadCustomerHistory(it.customerId) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Add Purchase Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Record Customer Purchase",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBluePrimary
                )

                // Select Existing Customer
                ExposedDropdownMenuBox(
                    expanded = customerDropdownExpanded,
                    onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCustomer?.let { "${it.name} (${it.customerId})" } ?: "Select Customer",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Existing Customer *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = customerDropdownExpanded,
                        onDismissRequest = { customerDropdownExpanded = false }
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text("${customer.name} (${customer.customerId})") },
                                onClick = {
                                    selectedCustomer = customer
                                    customerDropdownExpanded = false
                                    loadCustomerHistory(customer.customerId)
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Product Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) quantityText = input
                        },
                        label = { Text("Quantity *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = pricePerUnitText,
                        onValueChange = { pricePerUnitText = it },
                        label = { Text("Price / Unit (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Auto-calculated Total Amount
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBlueSurface, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkBluePrimary
                    )
                    Text(
                        text = currencyFormat.format(totalAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlueAccent
                    )
                }

                OutlinedTextField(
                    value = purchaseDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Purchase Date *") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Purchase Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = paymentDropdownExpanded,
                    onExpandedChange = { paymentDropdownExpanded = !paymentDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = paymentDropdownExpanded,
                        onDismissRequest = { paymentDropdownExpanded = false }
                    ) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    paymentMethod = method
                                    paymentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                statusMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = if (isError) ErrorCrimson else SuccessGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        val cust = selectedCustomer
                        if (cust == null) {
                            isError = true
                            statusMessage = "Please select a customer first."
                            return@Button
                        }
                        if (productName.isBlank()) {
                            isError = true
                            statusMessage = "Please enter product name."
                            return@Button
                        }
                        if (quantity <= 0 || unitPrice <= 0.0) {
                            isError = true
                            statusMessage = "Quantity and unit price must be greater than zero."
                            return@Button
                        }
                        scope.launch {
                            val purId = firestoreRepository.generateNextPurchaseId()
                            val purchase = Purchase(
                                purchaseId = purId,
                                customerId = cust.customerId,
                                customerName = cust.name,
                                productName = productName.trim(),
                                category = category,
                                quantity = quantity,
                                pricePerUnit = unitPrice,
                                totalAmount = totalAmount,
                                purchaseDate = purchaseDate,
                                paymentMethod = paymentMethod
                            )
                            val res = firestoreRepository.addPurchase(purchase)
                            if (res.isSuccess) {
                                isError = false
                                statusMessage = "Purchase $purId saved and linked to ${cust.customerId}!"
                                productName = ""
                                quantityText = "1"
                                pricePerUnitText = ""
                                loadCustomerHistory(cust.customerId)
                            } else {
                                isError = true
                                statusMessage = res.exceptionOrNull()?.message
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBluePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Save Purchase Record", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Individual Customer Purchase History Section
        Text(
            text = selectedCustomer?.let { "Purchase History — ${it.name} (${it.customerId})" }
                ?: "Individual Customer Purchase History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBluePrimary
        )

        if (customerPurchases.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Text(
                    text = "No purchases recorded yet for this customer.",
                    color = SlateText,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            customerPurchases.forEach { purchase ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = purchase.productName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = DarkBluePrimary
                            )
                            Text(
                                text = currencyFormat.format(purchase.totalAmount),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlueAccent
                            )
                        }
                        Text(
                            text = "${purchase.purchaseId} · ${purchase.category} · Qty ${purchase.quantity} × ${currencyFormat.format(purchase.pricePerUnit)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateText
                        )
                        Text(
                            text = "Date: ${purchase.purchaseDate} · Paid via ${purchase.paymentMethod}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateText
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            purchaseDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
