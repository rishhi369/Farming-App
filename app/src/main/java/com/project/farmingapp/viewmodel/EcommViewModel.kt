package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EcommViewModel : ViewModel() {
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var ecommLiveData = MutableLiveData<List<DocumentSnapshot>>()
    var specificCategoryItems = MutableLiveData<List<DocumentSnapshot>>()
    var specificItem = MutableLiveData<DocumentSnapshot>()
    private var seededProducts = false

    fun loadAllEcommItems(): MutableLiveData<List<DocumentSnapshot>> {
        if (!seededProducts) {
            seedSampleProducts()
            return ecommLiveData
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()

        firebaseFireStore.collection("products").get()
            .addOnSuccessListener {
                ecommLiveData.value = it.documents
                Log.d("ecommviewmodel", it.documents.toString())

            }
            .addOnFailureListener {
                Log.d("ecommviewmodel", it.message ?: "Failed loading products")
            }
        return ecommLiveData
    }

    fun loadSpecificTypeEcomItem(itemType: String) {
        firebaseFireStore = FirebaseFirestore.getInstance()

        firebaseFireStore.collection("products")
            .whereEqualTo("type", itemType)
            .get()
            .addOnSuccessListener {
                ecommLiveData.value = it.documents
                Log.d("ecommviewmodel", it.documents.toString())

            }
            .addOnFailureListener {
                Log.d("ecommviewmodel", it.message ?: "Failed loading products")
            }

    }

    fun getSpecificCategoryItems(itemType: String): MutableLiveData<List<DocumentSnapshot>> {
        firebaseFireStore.collection("products")
            .whereEqualTo("type", itemType)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    specificCategoryItems.value = it.result!!.documents
                    Log.d("EcommViewModel", it.result!!.documents.toString())
                }
            }
            .addOnFailureListener {
                Log.e("EcommViewModel", "Error Loading Specific Category Items")
            }
        return specificCategoryItems
    }

    fun getSpecificItem(itemID: String): MutableLiveData<DocumentSnapshot> {
        firebaseFireStore.collection("products")
            .document(itemID)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("EcommViewModel", it.result!!.data.toString())
                    specificItem.value = it.result
                } else {
                    Log.e("EcommViewModel", "Failed Getting Data")
                }
            }.addOnFailureListener {
                Log.e("EcommViewModel", "Failed Getting Data")
        }
        return specificItem
    }

    private fun seedSampleProducts() {
        seededProducts = true

        val samples = listOf(
            "organic_fertilizer" to hashMapOf<String, Any>(
                "title" to "Water Soluble Fertilizer 19:19:19",
                "price" to "649",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.7,
                "type" to "fertilizer",
                "shortDesc" to "Balanced fertilizer widely used in grape, onion, and vegetable farming around Nashik.",
                "longDesc" to "A quick-dissolving balanced fertilizer that supports vegetative growth, flowering, and fruit development for vineyards and vegetable crops.",
                "howtouse" to "Mix in water and apply through drip or foliar spray as recommended by crop stage.",
                "delCharge" to "45",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1589923188900-85dae523342b?w=800"),
                "attributes" to hashMapOf(
                    "Weight" to arrayListOf("1kg 649", "5kg 2899", "10kg 5499"),
                    "Suitable For" to "Grapes, onion, tomato, capsicum",
                    "Application" to "Drip / foliar spray"
                )
            ),
            "neem_pesticide" to hashMapOf<String, Any>(
                "title" to "Downy Mildew Control Fungicide",
                "price" to "899",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.6,
                "type" to "pestiside",
                "shortDesc" to "Targeted crop protection for grape vineyards during humid disease periods.",
                "longDesc" to "This fungicide is useful for grape growers facing downy mildew pressure and helps protect leaves, bunches, and overall crop quality.",
                "howtouse" to "Dilute in clean water and spray uniformly in the early morning or evening.",
                "delCharge" to "50",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1592982537447-7440770cbfc9?w=800"),
                "attributes" to hashMapOf(
                    "Size" to arrayListOf("250g 899", "500g 1649", "1kg 3099"),
                    "Crop" to "Grapes",
                    "Form" to "Liquid"
                )
            ),
            "garden_sprayer" to hashMapOf<String, Any>(
                "title" to "Battery Operated Knapsack Sprayer",
                "price" to "3899",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.5,
                "type" to "irrigation",
                "shortDesc" to "Useful for vineyards, onion plots, and vegetable farms requiring regular spraying.",
                "longDesc" to "A rechargeable knapsack sprayer that reduces labor and provides even spray coverage across rows of grapes and open-field vegetables.",
                "howtouse" to "Charge the battery, fill the tank, and spray using the selected nozzle setting.",
                "delCharge" to "120",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1581579188871-45ea61f2a1f8?w=800"),
                "attributes" to hashMapOf(
                    "Capacity" to arrayListOf("16L 3899", "20L 4499"),
                    "Battery" to "12V rechargeable",
                    "Use" to "Spraying / plant protection"
                )
            ),
            "onion_seed_red" to hashMapOf<String, Any>(
                "title" to "Red Onion Hybrid Seed Pack",
                "price" to "499",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.4,
                "type" to "seed",
                "shortDesc" to "Popular onion seed pack suitable for Nashik onion growers.",
                "longDesc" to "Selected hybrid onion seed pack for uniform bulb size, strong germination, and field suitability in onion-growing belts.",
                "howtouse" to "Use in nursery trays or prepared seed beds before transplanting to the main field.",
                "delCharge" to "35",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?w=800"),
                "attributes" to hashMapOf(
                    "Pack Size" to arrayListOf("100g 499", "250g 1099"),
                    "Crop" to "Onion",
                    "Season" to "Rabi / late kharif"
                )
            ),
            "grape_pruning_shear" to hashMapOf<String, Any>(
                "title" to "Grape Vineyard Pruning Shear",
                "price" to "799",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.6,
                "type" to "grapes",
                "shortDesc" to "Precision pruning tool for grape bunch and branch management.",
                "longDesc" to "A durable shear designed for grape vineyards, helpful for pruning, canopy management, and bunch maintenance.",
                "howtouse" to "Use for clean cuts on young branches and bunch stems after sanitizing the blade.",
                "delCharge" to "40",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1597852074816-d933c7d2b988?w=800"),
                "attributes" to hashMapOf(
                    "Blade" to "Hardened steel",
                    "Use" to "Pruning",
                    "Grip" to "Anti-slip"
                )
            ),
            "drip_kit_field" to hashMapOf<String, Any>(
                "title" to "Drip Irrigation Starter Kit",
                "price" to "2499",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.5,
                "type" to "irrigation",
                "shortDesc" to "Water-saving irrigation kit for vineyards and vegetable plots.",
                "longDesc" to "Helps reduce water wastage and supports efficient fertigation, especially useful for grape and vegetable farmers in dry conditions.",
                "howtouse" to "Lay lateral lines along crop rows and connect to the main pipeline and filter unit.",
                "delCharge" to "150",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1563514227147-6d2ff665a6a0?w=800"),
                "attributes" to hashMapOf(
                    "Coverage" to arrayListOf("0.25 acre 2499", "0.5 acre 4299"),
                    "Use" to "Drip irrigation",
                    "Suitable For" to "Grapes, onion, tomato"
                )
            ),
            "micronutrient_mix" to hashMapOf<String, Any>(
                "title" to "Micronutrient Mix for Grapes and Onion",
                "price" to "549",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.3,
                "type" to "fertilizer",
                "shortDesc" to "Multi-micronutrient support for leaf health, fruit quality, and bulb development.",
                "longDesc" to "A balanced micronutrient mix that supports chlorophyll formation, flowering, fruit quality, and healthier crop growth in orchard and field crops.",
                "howtouse" to "Use as foliar spray or soil application according to crop stage.",
                "delCharge" to "35",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1466692476868-aef1dfb1e735?w=800"),
                "attributes" to hashMapOf(
                    "Weight" to arrayListOf("500g 549", "1kg 999"),
                    "Suitable For" to "Grapes, onion, pomegranate",
                    "Form" to "Powder"
                )
            ),
            "thrips_control" to hashMapOf<String, Any>(
                "title" to "Thrips and Sucking Pest Control",
                "price" to "699",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.4,
                "type" to "pestiside",
                "shortDesc" to "Protection spray commonly needed in onion and vegetable farming.",
                "longDesc" to "Helps manage thrips and sucking pests that affect crop vigor, leaf health, and market quality in onion and vegetable fields.",
                "howtouse" to "Mix in water and spray in recommended quantity during low-wind hours.",
                "delCharge" to "40",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1501004318641-b39e6451bec6?w=800"),
                "attributes" to hashMapOf(
                    "Size" to arrayListOf("250ml 699", "500ml 1299"),
                    "Suitable For" to "Onion, chilli, vegetables",
                    "Mode" to "Foliar spray"
                )
            ),
            "tomato_seed_hybrid" to hashMapOf<String, Any>(
                "title" to "Hybrid Tomato Seed Pack",
                "price" to "399",
                "retailer" to "Nashik Agri Center",
                "availability" to "In Stock",
                "rating" to 4.2,
                "type" to "seed",
                "shortDesc" to "High-germination tomato seed for open-field and protected cultivation.",
                "longDesc" to "A reliable hybrid tomato seed pack suitable for farmers growing tomato in Nashik and nearby vegetable belts.",
                "howtouse" to "Sow in nursery trays and transplant healthy seedlings into prepared field beds.",
                "delCharge" to "30",
                "imageUrl" to arrayListOf("https://images.unsplash.com/photo-1546470427-e5ac89cd0b98?w=800"),
                "attributes" to hashMapOf(
                    "Pack Size" to arrayListOf("50g 399", "100g 749"),
                    "Crop" to "Tomato",
                    "Use" to "Nursery raising"
                )
            )
        )

        var completed = 0
        samples.forEach { (id, data) ->
            firebaseFireStore.collection("products").document(id)
                .set(data)
                .addOnCompleteListener {
                    completed += 1
                    if (completed == samples.size) {
                        loadAllEcommItems()
                    }
                }
        }
    }
}
