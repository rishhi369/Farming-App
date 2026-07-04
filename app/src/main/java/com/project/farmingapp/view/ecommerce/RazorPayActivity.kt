package com.project.farmingapp.view.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import kotlinx.android.synthetic.main.activity_razor_pay.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RazorPayActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())

    private var productId: String = ""
    private var itemCost: Int = 0
    private var quantity: Int = 1
    private var deliveryCost: Int = 0
    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_razor_pay)

        productId = intent.getStringExtra("productId").orEmpty()
        itemCost = intent.getStringExtra("itemCost")?.toIntOrNull() ?: 0
        quantity = intent.getStringExtra("quantity")?.toIntOrNull() ?: 1
        deliveryCost = intent.getStringExtra("deliveryCost")?.toIntOrNull() ?: 0
        totalPrice = itemCost * quantity + deliveryCost

        netValue.text = "Net Value: Rs $totalPrice"
        orderNowBtn.text = "Place Demo Order"

        orderNowBtn.setOnClickListener {
            val name = fullNamePrePay.text.toString().trim()
            val locality = localityPrePay.text.toString().trim()
            val city = cityPrePay.text.toString().trim()
            val state = statePrePay.text.toString().trim()
            val pincode = pincodePrePay.text.toString().trim()
            val mobile = mobileNumberPrePay.text.toString().trim()

            if (listOf(name, locality, city, state, pincode, mobile).any { it.isBlank() }) {
                Toast.makeText(this, "Please add all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            placeDemoOrder(name, locality, city, state, pincode, mobile)
        }
    }

    private fun placeDemoOrder(
        name: String,
        locality: String,
        city: String,
        state: String,
        pincode: String,
        mobile: String
    ) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (productId.isBlank()) {
            Toast.makeText(this, "Product details missing", Toast.LENGTH_LONG).show()
            return
        }

        orderNowBtn.isEnabled = false

        val deliveryDate = Calendar.getInstance().apply {
            add(Calendar.DATE, 5)
        }.time

        val orderData = hashMapOf<String, Any>(
            "name" to name,
            "locality" to locality,
            "city" to city,
            "state" to state,
            "pincode" to pincode,
            "mobile" to mobile,
            "time" to sdf.format(Date()),
            "createdAt" to System.currentTimeMillis(),
            "productId" to productId,
            "itemCost" to itemCost,
            "quantity" to quantity,
            "deliveryCost" to deliveryCost,
            "totalPrice" to totalPrice,
            "paymentMode" to "Demo",
            "paymentStatus" to "Demo order placed",
            "deliveryStatus" to "Arriving By: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(deliveryDate)}"
        )

        firestore.collection("users")
            .document(user.uid)
            .collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                orderNowBtn.isEnabled = true
                Toast.makeText(this, it.message ?: "Order failed", Toast.LENGTH_LONG).show()
            }
    }
}
