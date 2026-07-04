package com.project.farmingapp.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.view.user.UserFragment
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.nav_header.view.*
class UserDataViewModel : ViewModel() {

    var userliveData = MutableLiveData<DocumentSnapshot>()

    fun getUserData(userId: String) {
        val firebaseFireStore = FirebaseFirestore.getInstance()

        firebaseFireStore.collection("users").document(userId)
            .get()
            .addOnCompleteListener {
                val result = it.result
                if (it.isSuccessful && result != null && result.exists()) {
                    userliveData.value = result
                    return@addOnCompleteListener
                }

                val email = FirebaseAuth.getInstance().currentUser?.email
                if (!email.isNullOrEmpty() && email != userId) {
                    firebaseFireStore.collection("users").document(email)
                        .get()
                        .addOnSuccessListener { emailResult ->
                            userliveData.value = emailResult
                        }
                }
            }
    }

    fun updateUserField(context: Context, userID: String, about: String?, city: String?) {
        val updates = mutableMapOf<String, Any>()
        if (about != null) {
            updates["about"] = about
        }
        if (city != null) {
            updates["city"] = city
        }

        if (updates.isEmpty()) {
            return
        }

        val firebaseFireStore = FirebaseFirestore.getInstance()
        firebaseFireStore.collection("users").document(userID)
            .update(updates)
            .addOnSuccessListener {
                Log.d("UserDataViewModel", "User profile data updated")
                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                getUserData(userID)
            }
            .addOnFailureListener {
                Log.d("UserDataViewModel", "Failed to update profile data")
                Toast.makeText(context, "Failed to update profile. Try Again!", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteUserPost(userId: String, postId: String){
        val firebaseFirestore = FirebaseFirestore.getInstance()

        firebaseFirestore.collection("posts").document(postId)
            .delete()
            .addOnSuccessListener {
                Log.d("User Data View Model", "Post Deleted")
                UserProfilePostsViewModel().getAllPosts(userId)
                firebaseFirestore.collection("users").document(userId).update("posts", FieldValue.arrayRemove("${postId}"))
                    .addOnSuccessListener {
                        Log.d("UserDataViewModel", "Successfully Deleted User Doc Post")
                        getUserData(userId)
                    }
                    .addOnFailureListener{
                        Log.e("UserDataViewModel", "Failed to delete post from User Doc")
                    }
            }
            .addOnFailureListener {
                Log.d("User Data View Model", "Failed to delete post")
            }
    }
}
