package com.tithi.customerpurchasehistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tithi.customerpurchasehistory.navigation.AppNavGraph
import com.tithi.customerpurchasehistory.ui.theme.CustomerPurchaseHistoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomerPurchaseHistoryTheme {
                AppNavGraph()
            }
        }
    }
}
