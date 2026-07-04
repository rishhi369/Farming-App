package com.project.farmingapp.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class AuthRepository {

    lateinit var googleSignInClient: GoogleSignInClient
    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var firebaseDb: FirebaseFirestore
    val data = MutableLiveData<String>()
    fun signInWithEmail(
        email: String,
        password: String,
        otherData: HashMap<String, Serializable?>
    ): LiveData<String> {

        firebaseDb = FirebaseFirestore.getInstance()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                val uid = user?.uid

                if (uid.isNullOrEmpty()) {
                    data.value = "Unable to create user profile. Please try again."
                    return@addOnCompleteListener
                }

                val userData = HashMap(otherData)
                userData["uid"] = uid
                userData["email"] = email

                firebaseDb.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        data.value = "Success"
                    }
                    .addOnFailureListener { exception ->
                        Log.d("AuthRepo", exception.message ?: "Unknown profile creation error")
                        data.value = exception.message ?: "Failed to create user profile."
                    }

            } else {
                data.value = it.exception?.message ?: "Failed to create account."
            }
        }.addOnFailureListener {
            Log.d("AuthRepo", it.message ?: "Unknown auth error")
            data.value = it.message
        }
        return data
    }

    fun signInToGoogle(
        idToken: String,
        email: String,
        otherData: HashMap<String, Serializable?>
    ): LiveData<String> {
        firebaseDb = FirebaseFirestore.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid

                    if (uid.isNullOrEmpty()) {
                        data.value = "Unable to create user profile. Please try again."
                        return@addOnCompleteListener
                    }

                    val userDocRef = firebaseDb.collection("users").document(uid)

                    userDocRef.get().addOnSuccessListener {
                        if(it.exists()){
                            Log.d("User", "User Exists")
                            data.value = "Success"
                        } else{
                            Log.d("User", "User Does not Exists")
                            val userData = HashMap(otherData)
                            userData["uid"] = uid
                            userData["email"] = email

                            firebaseDb.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    data.value = "Success"
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("AuthRepo", exception.message ?: "Unknown profile creation error")
                                    data.value = exception.message ?: "Failed to create user profile."
                                }
                        }
                    }.addOnFailureListener { exception ->
                        Log.d("AuthRepo", exception.message ?: "Unknown profile lookup error")
                        data.value = exception.message ?: "Failed to load user profile."
                    }
                } else {
                    data.value = it.exception?.message ?: "Google sign-in failed."
                }
            }

        return data
    }


    //login
    fun logInWithEmail(
        email: String,
        password: String
    ): LiveData<String> {

        firebaseDb = FirebaseFirestore.getInstance()

        val data = MutableLiveData<String>()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                data.value = "Success"

            } else if (it.isCanceled) {
                data.value = "Failure"
            }

        }.addOnFailureListener {
            Log.d("AuthRepo", it.message ?: "Unknown auth error")
            data.value = it.message
        }
        return data
    }
}
