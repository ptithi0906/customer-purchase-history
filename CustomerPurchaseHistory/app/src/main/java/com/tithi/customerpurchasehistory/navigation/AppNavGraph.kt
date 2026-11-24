package com.tithi.customerpurchasehistory.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tithi.customerpurchasehistory.data.model.Customer
import com.tithi.customerpurchasehistory.data.repository.AuthRepository
import com.tithi.customerpurchasehistory.data.repository.FirestoreRepository
import com.tithi.customerpurchasehistory.ui.screens.CustomerFormScreen
import com.tithi.customerpurchasehistory.ui.screens.CustomerListScreen
import com.tithi.customerpurchasehistory.ui.screens.DashboardScreen
import com.tithi.customerpurchasehistory.ui.screens.LoginScreen
import com.tithi.customerpurchasehistory.ui.screens.PurchaseHistoryScreen
import com.tithi.customerpurchasehistory.ui.theme.DarkBluePrimary
import com.tithi.customerpurchasehistory.ui.theme.LightBlueSurface
import com.tithi.customerpurchasehistory.ui.theme.PureWhite

sealed class Routes(val route: String, val label: String) {
    data object Login : Routes("login", "Login")
    data object CustomerForm : Routes("customer_form", "Register")
    data object Dashboard : Routes("dashboard", "Dashboard")
    data object CustomerList : Routes("customer_list", "Customers")
    data object PurchaseHistory : Routes("purchase_history", "Purchases")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    authRepository: AuthRepository = remember { AuthRepository() },
    firestoreRepository: FirestoreRepository = remember { FirestoreRepository() }
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.Login.route

    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedPurchaseCustomerId by remember { mutableStateOf<String?>(null) }

    val startDestination = if (authRepository.currentUser != null) {
        Routes.CustomerForm.route
    } else {
        Routes.Login.route
    }

    val showChrome = currentRoute != Routes.Login.route

    Scaffold(
        topBar = {
            if (showChrome) {
                TopAppBar(
                    title = {
                        val title = when (currentRoute) {
                            Routes.CustomerForm.route -> "Customer Registration"
                            Routes.Dashboard.route -> "Dashboard"
                            Routes.CustomerList.route -> "Customer Management"
                            Routes.PurchaseHistory.route -> "Purchase History"
                            else -> "Customer Purchase History"
                        }
                        Text(title, color = PureWhite)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBluePrimary),
                    actions = {
                        IconButton(
                            onClick = {
                                authRepository.signOut()
                                navController.navigate(Routes.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = PureWhite)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                NavigationBar(containerColor = PureWhite) {
                    val items = listOf(
                        Triple(Routes.CustomerForm, Icons.Default.PersonAdd, "Register"),
                        Triple(Routes.Dashboard, Icons.Default.Dashboard, "Dashboard"),
                        Triple(Routes.CustomerList, Icons.Default.Group, "Customers"),
                        Triple(Routes.PurchaseHistory, Icons.Default.ReceiptLong, "Purchases")
                    )
                    items.forEach { (dest, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        containerColor = LightBlueSurface
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Login.route) {
                LoginScreen(
                    authRepository = authRepository,
                    firestoreRepository = firestoreRepository,
                    onLoginSuccess = {
                        // Professor requirement: navigate to Customer Registration Screen after login
                        navController.navigate(Routes.CustomerForm.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.CustomerForm.route) {
                CustomerFormScreen(
                    firestoreRepository = firestoreRepository,
                    editingCustomer = editingCustomer,
                    onClearEditingCustomer = { editingCustomer = null },
                    onViewAllCustomers = {
                        navController.navigate(Routes.CustomerList.route)
                    }
                )
            }

            composable(Routes.Dashboard.route) {
                DashboardScreen(
                    firestoreRepository = firestoreRepository,
                    userEmail = authRepository.currentUser?.email ?: "Student User",
                    onAddCustomer = {
                        editingCustomer = null
                        navController.navigate(Routes.CustomerForm.route)
                    },
                    onViewCustomers = {
                        navController.navigate(Routes.CustomerList.route)
                    },
                    onOpenPurchaseHistory = {
                        navController.navigate(Routes.PurchaseHistory.route)
                    }
                )
            }

            composable(Routes.CustomerList.route) {
                CustomerListScreen(
                    firestoreRepository = firestoreRepository,
                    onEditCustomer = { customer ->
                        editingCustomer = customer
                        navController.navigate(Routes.CustomerForm.route)
                    },
                    onViewCustomerPurchases = { customer ->
                        selectedPurchaseCustomerId = customer.customerId
                        navController.navigate(Routes.PurchaseHistory.route)
                    }
                )
            }

            composable(Routes.PurchaseHistory.route) {
                PurchaseHistoryScreen(
                    firestoreRepository = firestoreRepository,
                    preselectedCustomerId = selectedPurchaseCustomerId
                )
            }
        }
    }
}
