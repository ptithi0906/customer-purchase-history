package com.tithi.customerpurchasehistory.ui.screens

import android.util.Patterns
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.tithi.customerpurchasehistory.data.repository.FirestoreRepository
import com.tithi.customerpurchasehistory.ui.theme.DarkBluePrimary
import com.tithi.customerpurchasehistory.ui.theme.ErrorCrimson
import com.tithi.customerpurchasehistory.ui.theme.LightBlueSurface
import com.tithi.customerpurchasehistory.ui.theme.OffWhiteBackground
import com.tithi.customerpurchasehistory.ui.theme.PureWhite
import com.tithi.customerpurchasehistory.ui.theme.RoyalBlueAccent
import com.tithi.customerpurchasehistory.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    firestoreRepository: FirestoreRepository,
    editingCustomer: Customer?,
    onClearEditingCustomer: () -> Unit,
    onViewAllCustomers: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var customerId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var isEditMode by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    fun resetForm() {
        name = ""
        phoneNo = ""
        dob = ""
        email = ""
        address = ""
        isEditMode = false
        statusMessage = null
        isError = false
        onClearEditingCustomer()
        scope.launch {
            customerId = firestoreRepository.generateNextCustomerId()
        }
    }

    LaunchedEffect(editingCustomer) {
        if (editingCustomer != null) {
            customerId = editingCustomer.customerId
            name = editingCustomer.name
            phoneNo = editingCustomer.phoneNo
            dob = editingCustomer.dob
            email = editingCustomer.email
            address = editingCustomer.address
            isEditMode = true
            statusMessage = null
        } else {
            customerId = firestoreRepository.generateNextCustomerId()
            isEditMode = false
        }
    }

    fun validateForm(): String? {
        if (customerId.isBlank()) return "Customer ID is not generated yet."
        if (name.trim().length < 2) return "Please enter customer's full name."
        if (!phoneNo.trim().matches(Regex("^[0-9]{10}$"))) {
            return "Phone Number must be exactly 10 digits."
        }
        if (dob.isBlank()) return "Please select Date of Birth using the date picker."
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Please enter a valid email address."
        }
        if (address.trim().length < 5) return "Please enter a complete address."
        return null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isEditMode) "Update Customer Record" else "Customer Registration Form",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkBluePrimary
                )
                Text(
                    text = "All customer records are validated and stored in Cloud Firestore (customers collection).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Auto-generated Customer ID (Read-Only)
                OutlinedTextField(
                    value = customerId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Customer ID (Auto-Generated)") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneNo,
                    onValueChange = { input ->
                        if (input.length <= 10 && input.all { it.isDigit() }) {
                            phoneNo = input
                        }
                    },
                    label = { Text("Phone Number (10 digits) *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // DOB using Material 3 DatePickerDialog
                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date of Birth (DOB) *") },
                    placeholder = { Text("Select Date of Birth") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Residential / Billing Address *") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                statusMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = if (isError) ErrorCrimson else SuccessGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Save Customer & Update Customer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val validationError = validateForm()
                            if (validationError != null) {
                                isError = true
                                statusMessage = validationError
                                return@Button
                            }
                            isSaving = true
                            scope.launch {
                                val customer = Customer(
                                    customerId = customerId,
                                    name = name.trim(),
                                    phoneNo = phoneNo.trim(),
                                    dob = dob.trim(),
                                    email = email.trim(),
                                    address = address.trim()
                                )
                                val result = firestoreRepository.saveCustomer(customer)
                                isSaving = false
                                if (result.isSuccess) {
                                    isError = false
                                    statusMessage = "Customer ${customer.name} (${customer.customerId}) saved to Firebase!"
                                    name = ""
                                    phoneNo = ""
                                    dob = ""
                                    email = ""
                                    address = ""
                                    customerId = firestoreRepository.generateNextCustomerId()
                                } else {
                                    isError = true
                                    statusMessage = result.exceptionOrNull()?.message ?: "Save failed."
                                }
                            }
                        },
                        enabled = !isSaving && !isEditMode,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBluePrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Customer")
                    }

                    Button(
                        onClick = {
                            val validationError = validateForm()
                            if (validationError != null) {
                                isError = true
                                statusMessage = validationError
                                return@Button
                            }
                            isSaving = true
                            scope.launch {
                                val customer = Customer(
                                    customerId = customerId,
                                    name = name.trim(),
                                    phoneNo = phoneNo.trim(),
                                    dob = dob.trim(),
                                    email = email.trim(),
                                    address = address.trim()
                                )
                                val result = firestoreRepository.updateCustomer(customer)
                                isSaving = false
                                if (result.isSuccess) {
                                    isError = false
                                    statusMessage = "Customer ${customer.customerId} updated successfully!"
                                    isEditMode = false
                                    onClearEditingCustomer()
                                } else {
                                    isError = true
                                    statusMessage = result.exceptionOrNull()?.message ?: "Update failed."
                                }
                            }
                        },
                        enabled = !isSaving && isEditMode,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlueAccent),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Update Customer")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { resetForm() },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear Form", color = DarkBluePrimary)
                    }

                    OutlinedButton(
                        onClick = onViewAllCustomers,
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = LightBlueSurface),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Customers", color = DarkBluePrimary)
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
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dob = sdf.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
