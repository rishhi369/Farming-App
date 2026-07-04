package com.project.farmingapp.view.user

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.farmingapp.R
import com.project.farmingapp.adapter.PostListUserProfileAdapter
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.UserDataViewModel
import com.project.farmingapp.viewmodel.UserProfilePostsViewModel
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment(), CellClickListener {

    private lateinit var postsViewModel: UserProfilePostsViewModel
    private lateinit var userDataViewModel: UserDataViewModel
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postsViewModel = ViewModelProviders.of(requireActivity())
            .get<UserProfilePostsViewModel>(UserProfilePostsViewModel::class.java)

        userDataViewModel = ViewModelProviders.of(requireActivity())
            .get<UserDataViewModel>(UserDataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        setupInitialUi()
        observeProfile()
        observePosts()

        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_LONG).show()
            return
        }

        userEmailUserProfileFrag.text = user.email.orEmpty()
        userDataViewModel.getUserData(user.uid)
        postsViewModel.getAllPosts(user.uid)

        imageEdit.setOnClickListener { startEditingProfile() }
        imageChecked.setOnClickListener { saveProfileEdits() }

        uploadProfilePictureImage.setOnClickListener {
            Toast.makeText(requireContext(), "Image upload is not configured for this demo.", Toast.LENGTH_SHORT).show()
        }
        uploadUserBackgroundImage.setOnClickListener {
            Toast.makeText(requireContext(), "Image upload is not configured for this demo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupInitialUi() {
        cityEditUserProfile.visibility = View.GONE
        aboutValueEditUserProfileFrag.visibility = View.GONE
        saveBtnAboutUserProfileFrag.visibility = View.GONE
        imageChecked.visibility = View.GONE
        uploadProgressBarProfile.visibility = View.GONE
        uploadBackProgressProfile.visibility = View.GONE
        uploadProfilePictureImage.visibility = View.GONE
        uploadUserBackgroundImage.visibility = View.GONE
        userProfilePostsRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeProfile() {
        userDataViewModel.userliveData.observe(viewLifecycleOwner, Observer { userDoc ->
            if (userDoc == null || !userDoc.exists()) {
                userNameUserProfileFrag.text = "User"
                userCityUserProfileFrag.text = "City: "
                userPostsCountUserProfileFrag.text = "Posts: 0"
                aboutValueUserProfileFrag.text = "Add your farming interests and experience."
                return@Observer
            }

            userNameUserProfileFrag.text = userDoc.getString("name").orEmpty().ifBlank { "User" }
            userCityUserProfileFrag.text = "City: ${userDoc.getString("city").orEmpty()}"
            userEmailUserProfileFrag.text = firebaseAuth.currentUser?.email.orEmpty()

            val about = userDoc.getString("about").orEmpty()
            aboutValueUserProfileFrag.text = about.ifBlank { "Add your farming interests and experience." }
            aboutValueUserProfileFrag.visibility = View.VISIBLE

            val posts = userDoc.get("posts") as? List<*> ?: emptyList<Any>()
            userPostsCountUserProfileFrag.text = "Posts: ${posts.size}"

            val profileImage = userDoc.getString("profileImage").orEmpty()
            if (profileImage.isNotBlank()) {
                Glide.with(requireContext()).load(profileImage).into(userImageUserFrag)
            } else {
                userImageUserFrag.setImageResource(R.drawable.ic_user_profile)
            }

            val backImage = userDoc.getString("backImage").orEmpty()
            if (backImage.isNotBlank()) {
                Glide.with(requireContext()).load(backImage).into(userBackgroundImage)
            } else {
                userBackgroundImage.setBackgroundResource(R.color.secondary)
            }
        })
    }

    private fun observePosts() {
        postsViewModel.liveData3.observe(viewLifecycleOwner, Observer { posts ->
            val safePosts = posts ?: arrayListOf()
            userPostsCountUserProfileFrag.text = "Posts: ${safePosts.size}"
            userProfilePostsRecycler.adapter =
                PostListUserProfileAdapter(requireContext(), safePosts, this)
        })
    }

    private fun startEditingProfile() {
        imageEdit.visibility = View.GONE
        imageChecked.visibility = View.VISIBLE

        cityEditUserProfile.setText(userCityUserProfileFrag.text.toString().removePrefix("City: ").trim())
        cityEditUserProfile.visibility = View.VISIBLE

        aboutValueEditUserProfileFrag.setText(aboutValueUserProfileFrag.text.toString())
        aboutValueEditUserProfileFrag.visibility = View.VISIBLE
        aboutValueUserProfileFrag.visibility = View.GONE
    }

    private fun saveProfileEdits() {
        val user = firebaseAuth.currentUser ?: return

        imageEdit.visibility = View.VISIBLE
        imageChecked.visibility = View.GONE
        cityEditUserProfile.visibility = View.GONE
        aboutValueEditUserProfileFrag.visibility = View.GONE
        aboutValueUserProfileFrag.visibility = View.VISIBLE

        userDataViewModel.updateUserField(
            requireContext(),
            user.uid,
            aboutValueEditUserProfileFrag.text.toString().trim(),
            cityEditUserProfile.text.toString().trim()
        )
    }

    override fun onCellClickListener(name: String) {
        val user = firebaseAuth.currentUser ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Your Post")
            .setMessage("Do you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                userDataViewModel.deleteUserPost(user.uid, name)
                postsViewModel.getAllPosts(user.uid)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
