package com.tithi.customerpurchasehistory.data

import com.google.firebase.Timestamp
import com.tithi.customerpurchasehistory.data.model.Customer
import com.tithi.customerpurchasehistory.data.model.Purchase

/**
 * Contains 10 realistic sample customer records and linked sample purchases
 * for college DBMS project demonstration and Firebase Firestore seeding.
 */
object DummyDataSeeder {

    val sampleCustomers: List<Customer> = listOf(
        Customer(
            customerId = "CUST-1001",
            name = "Rahul Patel",
            phoneNo = "9825011223",
            dob = "14/05/1996",
            email = "rahul.patel@example.com",
            address = "204, Shivalik Residency, Satellite, Ahmedabad, Gujarat 380015",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1002",
            name = "Priya Shah",
            phoneNo = "9898123456",
            dob = "22/08/1998",
            email = "priya.shah@example.com",
            address = "15, Parkview Apartments, Alkapuri, Vadodara, Gujarat 390007",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1003",
            name = "Amit Sharma",
            phoneNo = "9724098765",
            dob = "03/11/1992",
            email = "amit.sharma@example.com",
            address = "402, Silver Oak Towers, Vesu, Surat, Gujarat 395007",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1004",
            name = "Neha Patel",
            phoneNo = "9909112244",
            dob = "19/02/1999",
            email = "neha.patel@example.com",
            address = "8, Gulmohar Bungalows, Vidyanagar, Anand, Gujarat 388120",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1005",
            name = "Riya Desai",
            phoneNo = "9879543210",
            dob = "30/07/1997",
            email = "riya.desai@example.com",
            address = "108, Sapphire Heights, Kalawad Road, Rajkot, Gujarat 360005",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1006",
            name = "Krunal Mehta",
            phoneNo = "9687012398",
            dob = "11/12/1994",
            email = "krunal.mehta@example.com",
            address = "B-12, Navrang Society, Navrangpura, Ahmedabad, Gujarat 380009",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1007",
            name = "Pooja Joshi",
            phoneNo = "9824567890",
            dob = "25/03/2000",
            email = "pooja.joshi@example.com",
            address = "77, Sanskar Nagar, Sector 11, Gandhinagar, Gujarat 382011",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1008",
            name = "Jay Shah",
            phoneNo = "9925887766",
            dob = "09/09/1995",
            email = "jay.shah@example.com",
            address = "31, Diamond Plaza, Adajan, Surat, Gujarat 395009",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1009",
            name = "Meet Patel",
            phoneNo = "9712334455",
            dob = "17/01/2001",
            email = "meet.patel@example.com",
            address = "52, Sardar Patel Colony, Nadiad, Gujarat 387001",
            createdAt = Timestamp.now()
        ),
        Customer(
            customerId = "CUST-1010",
            name = "Diya Shah",
            phoneNo = "9898765432",
            dob = "06/06/1998",
            email = "diya.shah@example.com",
            address = "901, Emerald Crest, Prahlad Nagar, Ahmedabad, Gujarat 380015",
            createdAt = Timestamp.now()
        )
    )

    val samplePurchases: List<Purchase> = listOf(
        Purchase(
            purchaseId = "PUR-5001",
            customerId = "CUST-1001",
            customerName = "Rahul Patel",
            productName = "Samsung 27-inch Monitor",
            category = "Electronics",
            quantity = 1,
            pricePerUnit = 18500.0,
            totalAmount = 18500.0,
            purchaseDate = "12/10/2026",
            paymentMethod = "UPI",
            createdAt = Timestamp.now()
        ),
        Purchase(
            purchaseId = "PUR-5002",
            customerId = "CUST-1002",
            customerName = "Priya Shah",
            productName = "Sony Wireless Headphones",
            category = "Electronics",
            quantity = 2,
            pricePerUnit = 4200.0,
            totalAmount = 8400.0,
            purchaseDate = "18/10/2026",
            paymentMethod = "Credit Card",
            createdAt = Timestamp.now()
        ),
        Purchase(
            purchaseId = "PUR-5003",
            customerId = "CUST-1003",
            customerName = "Amit Sharma",
            productName = "Prestige Induction Cooktop",
            category = "Home & Kitchen",
            quantity = 1,
            pricePerUnit = 3250.0,
            totalAmount = 3250.0,
            purchaseDate = "24/10/2026",
            paymentMethod = "Debit Card",
            createdAt = Timestamp.now()
        ),
        Purchase(
            purchaseId = "PUR-5004",
            customerId = "CUST-1001",
            customerName = "Rahul Patel",
            productName = "Logitech Mechanical Keyboard",
            category = "Electronics",
            quantity = 1,
            pricePerUnit = 3600.0,
            totalAmount = 3600.0,
            purchaseDate = "02/11/2026",
            paymentMethod = "UPI",
            createdAt = Timestamp.now()
        ),
        Purchase(
            purchaseId = "PUR-5005",
            customerId = "CUST-1005",
            customerName = "Riya Desai",
            productName = "Nike Running Shoes",
            category = "Footwear",
            quantity = 1,
            pricePerUnit = 5499.0,
            totalAmount = 5499.0,
            purchaseDate = "09/11/2026",
            paymentMethod = "Net Banking",
            createdAt = Timestamp.now()
        ),
        Purchase(
            purchaseId = "PUR-5006",
            customerId = "CUST-1010",
            customerName = "Diya Shah",
            productName = "Organic Grocery Essentials Pack",
            category = "Groceries",
            quantity = 3,
            pricePerUnit = 1250.0,
            totalAmount = 3750.0,
            purchaseDate = "14/11/2026",
            paymentMethod = "Cash",
            createdAt = Timestamp.now()
        )
    )
}
