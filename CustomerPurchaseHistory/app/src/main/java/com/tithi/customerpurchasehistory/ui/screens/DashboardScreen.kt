package com.tithi.customerpurchasehistory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tithi.customerpurchasehistory.data.repository.FirestoreRepository
import com.tithi.customerpurchasehistory.ui.theme.DarkBluePrimary
import com.tithi.customerpurchasehistory.ui.theme.LightBlueSurface
import com.tithi.customerpurchasehistory.ui.theme.OffWhiteBackground
import com.tithi.customerpurchasehistory.ui.theme.PureWhite
import com.tithi.customerpurchasehistory.ui.theme.RoyalBlueAccent
import com.tithi.customerpurchasehistory.ui.theme.SlateText
import com.tithi.customerpurchasehistory.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    firestoreRepository: FirestoreRepository,
    userEmail: String,
    onAddCustomer: () -> Unit,
    onViewCustomers: () -> Unit,
    onOpenPurchaseHistory: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var totalCustomers by remember { mutableIntStateOf(0) }
    var totalPurchases by remember { mutableIntStateOf(0) }
    var totalSales by remember { mutableDoubleStateOf(0.0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    fun loadDashboardStats() {
        scope.launch {
            val customers = firestoreRepository.getAllCustomers().getOrDefault(emptyList())
            val purchases = firestoreRepository.getAllPurchases().getOrDefault(emptyList())
            totalCustomers = customers.size
            totalPurchases = purchases.size
            totalSales = purchases.sumOf { it.totalAmount }
        }
    }

    LaunchedEffect(Unit) {
        loadDashboardStats()
    }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBluePrimary)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Store Analytics Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Signed in as $userEmail",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightBlueSurface
                )
            }
        }

        // Metric Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Customers",
                value = totalCustomers.toString(),
                icon = Icons.Default.Group,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total Purchases",
                value = totalPurchases.toString(),
                icon = Icons.Default.ShoppingBag,
                modifier = Modifier.weight(1f)
            )
        }

        MetricCard(
            title = "Total Sales Revenue",
            value = currencyFormat.format(totalSales),
            icon = Icons.Default.CurrencyRupee,
            modifier = Modifier.fillMaxWidth()
        )

        // Quick Actions
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkBluePrimary
        )

        Button(
            onClick = onAddCustomer,
            colors = ButtonDefaults.buttonColors(containerColor = DarkBluePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Add New Customer", fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onViewCustomers,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlueAccent),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Group, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("View Customers", fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onOpenPurchaseHistory,
            colors = ButtonDefaults.buttonColors(containerColor = DarkBluePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Purchase History", fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
            onClick = {
                scope.launch {
                    val res = firestoreRepository.seedDummyDataIfRequested(onlyIfEmpty = false)
                    feedbackMessage = if (res.isSuccess) {
                        "Seeded ${res.getOrDefault(0)} sample customer records & demo purchases in Firestore."
                    } else {
                        res.exceptionOrNull()?.message
                    }
                    loadDashboardStats()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = DarkBluePrimary)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Seed 10 Sample Customers in Firestore", color = DarkBluePrimary)
        }

        feedbackMessage?.let { msg ->
            Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                color = SuccessGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RoyalBlueAccent,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateText
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkBluePrimary
                )
            }
        }
    }
}
