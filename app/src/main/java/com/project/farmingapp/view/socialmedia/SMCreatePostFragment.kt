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
import com.project.farmingapp.databinding.FragmentSMCreatePostBinding

class SMCreatePostFragment : Fragment() {

    private val authUser: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val categories = listOf("Crop Problem", "Farming Tip", "Market Update", "Question", "Government Scheme")

    private var _binding: FragmentSMCreatePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSMCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Create Post"

        setLoading(false)

        binding.uploadImagePreview.visibility = View.GONE
        binding.createPostTitle.text = "Create Text Post"
        binding.postCategorySpinnerSM.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        binding.createPostBtnSM.setOnClickListener {
            val title = binding.postTitleSM.text.toString().trim()
            val description = binding.descPostSM.text.toString().trim()
            val category = binding.postCategorySpinnerSM.selectedItem?.toString() ?: categories.first()

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
                        if (_binding != null) openPostList()
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
        if (_binding == null) return
        binding.progressCreatePost.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressTitle.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.createPostBtnSM.isEnabled = !isLoading
    }
}
