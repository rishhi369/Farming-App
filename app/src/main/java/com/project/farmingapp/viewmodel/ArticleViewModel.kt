package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class ArticleViewModel : ViewModel() {

    var message1 = MutableLiveData<HashMap<String, Any>>()
    var message2 = MutableLiveData<String>()
    var message3 = MutableLiveData<List<DocumentSnapshot>>()
    var articleListener: ArticleListener? = null
    private var todoLiveData: LiveData<HashMap<String, Any>>? = null
    lateinit var firebaseDb: FirebaseFirestore
    lateinit var firebaseStorage: FirebaseStorage
    private val seededCollections = mutableSetOf<String>()

    fun getArticle(): MutableLiveData<HashMap<String, Any>> {
        Log.d("ArticleViewModelGet", message1.value.toString())
        return message1
    }

    fun getMyArticle(name: String) {

        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()

        Log.d("ArticleRepo1", "Ss")
        firebaseDb.collection("article_fruits").document("${name}")
            .get()
            .addOnSuccessListener {
                message1.value = it.data as HashMap<String, Any>?
                Log.d("ArticleViewModelDirect", message1.value.toString())
            }
            .addOnFailureListener {
                Log.d("ArticleRepo3", "ss")
            }
    }

    fun getAllArticles(name: String){
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseDb.collection(name).get().addOnSuccessListener {
            if (it.isEmpty && !seededCollections.contains(name)) {
                seedSampleArticles(name)
                return@addOnSuccessListener
            }

            message3.value = it.documents
            Log.d("I'm called4", "Yes")
        }.addOnFailureListener {
            Log.d("ArticleViewModel", it.message ?: "Failed loading articles")
        }

    }

    fun updateArticle(data: HashMap<String, Any>) {
        Log.d("ArticleViewModel", data.toString())
        message1.value = data
    }

    private fun seedSampleArticles(collectionName: String) {
        seededCollections.add(collectionName)

        val readableName = when (collectionName) {
            "article_plants" -> "Healthy Crop Planning"
            "article_methods" -> "Drip Irrigation Basics"
            "article_diseases" -> "Early Leaf Disease Control"
            "article_yojnas" -> "Farmer Support Schemes"
            else -> "Mango Cultivation"
        }

        val sample = hashMapOf<String, Any>(
            "title" to readableName,
            "description" to "A practical guide for farmers with simple steps, care tips, and field-friendly advice.",
            "process" to "Prepare the soil, choose healthy inputs, monitor growth weekly, and respond early to pests or disease.",
            "soil" to "Well-drained fertile soil with regular organic matter gives the best results.",
            "state" to "Suitable for many Indian farming regions with local adjustments.",
            "images" to arrayListOf("https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800"),
            "diseases" to arrayListOf("Leaf spot", "Root rot", "Nutrient deficiency"),
            "attributes" to hashMapOf(
                "Temperature" to "20°C - 32°C",
                "Time" to "Season dependent",
                "Weight" to "Varies by crop",
                "Vitamins" to "Crop specific",
                "Tree Height" to "Depends on variety",
                "growthTime" to "2 - 6 months"
            )
        )

        firebaseDb.collection(collectionName).document(readableName)
            .set(sample)
            .addOnSuccessListener {
                getAllArticles(collectionName)
            }
            .addOnFailureListener {
                Log.d("ArticleViewModel", it.message ?: "Failed seeding articles")
                message3.value = emptyList()
            }
    }

}
