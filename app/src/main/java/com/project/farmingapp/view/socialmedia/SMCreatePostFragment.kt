package com.project.farmingapp.view.socialmedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import kotlinx.android.synthetic.main.fragment_s_m_create_post.*

class SMCreatePostFragment : Fragment() {

    private val authUser: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val categories = listOf("Crop Problem", "Farming Tip", "Market Update", "Question", "Government Scheme")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_s_m_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Create Post"

        setLoading(false)

        uploadImagePreview.visibility = View.GONE
        createPostTitle.text = "Create Text Post"
        postCategorySpinnerSM.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        createPostBtnSM.setOnClickListener {
            val title = postTitleSM.text.toString().trim()
            val description = descPostSM.text.toString().trim()
            val category = postCategorySpinnerSM.selectedItem?.toString() ?: categories.first()

            when {
                authUser.currentUser == null -> {
                    Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show()
                }
                title.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT).show()
                }
                description.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter description", Toast.LENGTH_SHORT).show()
                }
                else -> createTextPost(title, description, category)
            }
        }
    }

    private fun createTextPost(title: String, description: String, category: String) {
        val user = authUser.currentUser ?: return
        setLoading(true)

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { userDoc ->
                val userName = user.displayName
                    ?: userDoc.getString("name")
                    ?: user.email?.substringBefore("@")
                    ?: "Farmer"

                val postData = hashMapOf<String, Any>(
                    "userID" to user.uid,
                    "name" to userName,
                    "timeStamp" to System.currentTimeMillis(),
                    "title" to title,
                    "description" to description,
                    "uploadType" to "",
                    "category" to category,
                    "likes" to 0,
                    "likedBy" to emptyList<String>(),
                    "commentsCount" to 0
                )

                savePost(user.uid, postData)
            }
            .addOnFailureListener {
                val userName = user.displayName ?: user.email?.substringBefore("@") ?: "Farmer"
                val postData = hashMapOf<String, Any>(
                    "userID" to user.uid,
                    "name" to userName,
                    "timeStamp" to System.currentTimeMillis(),
                    "title" to title,
                    "description" to description,
                    "uploadType" to "",
                    "category" to category,
                    "likes" to 0,
                    "likedBy" to emptyList<String>(),
                    "commentsCount" to 0
                )

                savePost(user.uid, postData)
            }
    }

    private fun savePost(uid: String, postData: HashMap<String, Any>) {
        db.collection("posts")
            .add(postData)
            .addOnSuccessListener { documentReference ->
                db.collection("users")
                    .document(uid)
                    .update("posts", FieldValue.arrayUnion(documentReference.id))
                    .addOnCompleteListener {
                        Toast.makeText(requireContext(), "Post created", Toast.LENGTH_LONG).show()
                        setLoading(false)
                        openPostList()
                    }
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(
                    requireContext(),
                    it.message ?: "Error saving post",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun openPostList() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, SocialMediaPostsFragment(), "smPostList")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun setLoading(isLoading: Boolean) {
        progress_create_post.visibility = if (isLoading) View.VISIBLE else View.GONE
        progressTitle.visibility = if (isLoading) View.VISIBLE else View.GONE
        createPostBtnSM.isEnabled = !isLoading
    }
}
