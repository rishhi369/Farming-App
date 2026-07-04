package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class YojnaViewModel: ViewModel() {

    lateinit var firebaseDb: FirebaseFirestore
    lateinit var firebaseStorage: FirebaseStorage
    var msg = MutableLiveData<HashMap<String, Any>>()
    var message3 = MutableLiveData<List<DocumentSnapshot>>()
    private var seededYojnas = false


    fun getYojna(name: String) {

        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()


        firebaseDb.collection("yojnas").document("${name}")
            .get()
            .addOnSuccessListener {
                msg.value = HashMap(it.data ?: emptyMap())
                Log.d("YojnaViewModel", msg.value.toString())
            }
            .addOnFailureListener {
                Log.d("YojnaViewModel", "ss")
            }
    }

    fun getAllYojna(name: String){
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseDb.collection(name).get().addOnSuccessListener {
            if (it.isEmpty && !seededYojnas) {
                seedSampleYojnas()
                return@addOnSuccessListener
            }

            message3.value = it.documents
            Log.d("I'm called4", "Yes")
        }

    }
    fun updateArticle(data: HashMap<String, Any>) {
        Log.d("ArticleViewModel", data.toString())
        msg.value = data
    }

    private fun seedSampleYojnas() {
        seededYojnas = true
        val samples = listOf(
            "pm_kisan" to hashMapOf<String, Any>(
                "title" to "PM-KISAN Samman Nidhi",
                "status" to "Active",
                "launch" to "2019",
                "headedBy" to "Government of India",
                "budget" to "Income support scheme",
                "description" to "PM-KISAN provides income support to eligible farmer families through direct benefit transfer.",
                "eligibility" to arrayListOf("Farmer family with cultivable land", "Valid Aadhaar and bank account", "As per government guidelines"),
                "documents" to arrayListOf("Aadhaar card", "Bank account details", "Land record"),
                "objective" to arrayListOf("Support small and marginal farmers", "Improve farm input affordability", "Provide direct financial assistance"),
                "website" to "https://pmkisan.gov.in/",
                "image" to "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=900"
            ),
            "soil_health_card" to hashMapOf<String, Any>(
                "title" to "Soil Health Card Scheme",
                "status" to "Active",
                "launch" to "2015",
                "headedBy" to "Ministry of Agriculture",
                "budget" to "Government funded",
                "description" to "The scheme helps farmers understand soil nutrient status and use fertilizers more effectively.",
                "eligibility" to arrayListOf("Farmers with agricultural land", "Soil sample collection as per local process"),
                "documents" to arrayListOf("Farmer identity proof", "Land details", "Contact information"),
                "objective" to arrayListOf("Promote balanced fertilizer use", "Improve soil health", "Increase crop productivity"),
                "website" to "https://soilhealth.dac.gov.in/",
                "image" to "https://images.unsplash.com/photo-1492496913980-501348b61469?w=900"
            )
        )

        var completed = 0
        samples.forEach { (id, data) ->
            firebaseDb.collection("yojnas").document(id)
                .set(data)
                .addOnCompleteListener {
                    completed += 1
                    if (completed == samples.size) {
                        getAllYojna("yojnas")
                    }
                }
        }
    }
}
