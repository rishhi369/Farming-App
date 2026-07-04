package com.project.farmingapp.view.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.databinding.ActivityRazorPayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RazorPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRazorPayBinding
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
        binding = ActivityRazorPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId").orEmpty()
        itemCost = intent.getStringExtra("itemCost")?.toIntOrNull() ?: 0
        quantity = intent.getStringExtra("quantity")?.toIntOrNull() ?: 1
        deliveryCost = intent.getStringExtra("deliveryCost")?.toIntOrNull() ?: 0
        totalPrice = itemCost * quantity + deliveryCost

        binding.netValue.text = "Net Value: Rs $totalPrice"
        binding.orderNowBtn.text = "Place Demo Order"

        binding.orderNowBtn.setOnClickListener {
            val name = binding.fullNamePrePay.text.toString().trim()
            val locality = binding.localityPrePay.text.toString().trim()
            val city = binding.cityPrePay.text.toString().trim()
            val state = binding.statePrePay.text.toString().trim()
            val pincode = binding.pincodePrePay.text.toString().trim()
            val mobile = binding.mobileNumberPrePay.text.toString().trim()

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

        binding.orderNowBtn.isEnabled = false

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
                binding.orderNowBtn.isEnabled = true
                Toast.makeText(this, it.message ?: "Order failed", Toast.LENGTH_LONG).show()
            }
    }
}
