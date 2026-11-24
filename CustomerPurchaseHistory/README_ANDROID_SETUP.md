# Customer Purchase History — Native Android DBMS Project
**Student:** Patel Tithi Pareshkumar  
**Tech Stack:** Kotlin · Jetpack Compose · Material Design 3 · Firebase Authentication · Cloud Firestore

---

## 1. Prerequisites
- **Android Studio** (Ladybug / Koala / Jellyfish or newer)
- **JDK 17** (bundled with Android Studio)
- **Google Account** for [Firebase Console](https://console.firebase.google.com/)
- Android Emulator (API 24+) or physical Android phone with USB Debugging enabled

---

## 2. Firebase Project Setup
1. Open [Firebase Console](https://console.firebase.google.com/) and click **Create a project**.
2. Name the project `CustomerPurchaseHistory` and complete creation.
3. Click the **Android icon** (`Add app`) on the Project Overview page:
   - **Android package name:** `com.tithi.customerpurchasehistory`
   - **App nickname:** `Customer Purchase History`
4. Download **`google-services.json`** and place it inside:
   `android_project/app/google-services.json`
5. In Firebase Console left menu:
   - Go to **Build → Authentication → Get Started → Sign-in method**
   - Enable **Email/Password** and click **Save**.
6. Go to **Build → Firestore Database → Create database**:
   - Start in **Production mode** (or Test mode temporarily) and choose your closest region (e.g., `asia-south1`).
7. Go to the **Rules** tab inside Firestore Database, paste the contents of `firestore.rules`, and click **Publish**.

---

## 3. Running in Android Studio
1. Extract the downloaded project folder (`CustomerPurchaseHistory`).
2. Verify `app/google-services.json` is present inside the `app/` directory.
3. Open **Android Studio → File → Open** and select the project root folder.
4. Wait for **Gradle Sync** to finish downloading dependencies.
5. Select an Android Emulator or connect your Android phone via USB.
6. Click **Run (▶)** to build and launch the app.

---

## 4. Demonstration Flow for College Viva
1. **Login / Sign-In Screen (First Page):** Create an account with **Sign Up** or log in with your email & password (or test **Forgot Password**).
2. **Automatic Sample Data Seeding:** Upon first login, the app automatically populates the `customers` collection in Cloud Firestore with all **10 sample customers** (Rahul Patel, Priya Shah, Amit Sharma, Neha Patel, Riya Desai, Krunal Mehta, Pooja Joshi, Jay Shah, Meet Patel, Diya Shah) and linked sample purchases.
3. **Customer Registration Form (Second Page):** Demonstrates auto-generated `CUST-XXXX` ID, Material 3 DOB DatePicker, phone/email validation, duplicate prevention, **Save Customer**, **Update Customer**, and **Clear Form**.
4. **Customer Management Screen:** Search customers by name, view complete details, edit any record, delete with confirmation, or refresh from Firestore.
5. **Purchase History Module:** Select an existing customer, add product, category, quantity, unit price, auto-calculated total, purchase date, and payment method; stores in `purchases` linked via `customerId`.
6. **Dashboard:** Displays live Total Customers, Total Purchases, and Total Sales revenue from Firestore.
