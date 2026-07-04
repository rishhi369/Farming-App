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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.farmingapp.R
import com.project.farmingapp.adapter.PostListUserProfileAdapter
import com.project.farmingapp.databinding.FragmentUserBinding
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.UserDataViewModel
import com.project.farmingapp.viewmodel.UserProfilePostsViewModel

class UserFragment : Fragment(), CellClickListener {

    private lateinit var postsViewModel: UserProfilePostsViewModel
    private lateinit var userDataViewModel: UserDataViewModel
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postsViewModel = ViewModelProvider(requireActivity())
            .get(UserProfilePostsViewModel::class.java)

        userDataViewModel = ViewModelProvider(requireActivity())
            .get(UserDataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        binding.userEmailUserProfileFrag.text = user.email.orEmpty()
        userDataViewModel.getUserData(user.uid)
        postsViewModel.getAllPosts(user.uid)

        binding.imageEdit.setOnClickListener { startEditingProfile() }
        binding.imageChecked.setOnClickListener { saveProfileEdits() }

        binding.uploadProfilePictureImage.setOnClickListener {
            Toast.makeText(requireContext(), "Image upload is not configured for this demo.", Toast.LENGTH_SHORT).show()
        }
        binding.uploadUserBackgroundImage.setOnClickListener {
            Toast.makeText(requireContext(), "Image upload is not configured for this demo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupInitialUi() {
        binding.cityEditUserProfile.visibility = View.GONE
        binding.aboutValueEditUserProfileFrag.visibility = View.GONE
        binding.saveBtnAboutUserProfileFrag.visibility = View.GONE
        binding.imageChecked.visibility = View.GONE
        binding.uploadProgressBarProfile.visibility = View.GONE
        binding.uploadBackProgressProfile.visibility = View.GONE
        binding.uploadProfilePictureImage.visibility = View.GONE
        binding.uploadUserBackgroundImage.visibility = View.GONE
        binding.userProfilePostsRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeProfile() {
        userDataViewModel.userliveData.observe(viewLifecycleOwner, Observer { userDoc ->
            if (userDoc == null || !userDoc.exists()) {
                binding.userNameUserProfileFrag.text = "User"
                binding.userCityUserProfileFrag.text = "City: "
                binding.userPostsCountUserProfileFrag.text = "Posts: 0"
                binding.aboutValueUserProfileFrag.text = "Add your farming interests and experience."
                return@Observer
            }

            binding.userNameUserProfileFrag.text = userDoc.getString("name").orEmpty().ifBlank { "User" }
            binding.userCityUserProfileFrag.text = "City: ${userDoc.getString("city").orEmpty()}"
            binding.userEmailUserProfileFrag.text = firebaseAuth.currentUser?.email.orEmpty()

            val about = userDoc.getString("about").orEmpty()
            binding.aboutValueUserProfileFrag.text = about.ifBlank { "Add your farming interests and experience." }
            binding.aboutValueUserProfileFrag.visibility = View.VISIBLE

            val posts = userDoc.get("posts") as? List<*> ?: emptyList<Any>()
            binding.userPostsCountUserProfileFrag.text = "Posts: ${posts.size}"

            val profileImage = userDoc.getString("profileImage").orEmpty()
            if (profileImage.isNotBlank()) {
                Glide.with(requireContext()).load(profileImage).into(binding.userImageUserFrag)
            } else {
                binding.userImageUserFrag.setImageResource(R.drawable.ic_user_profile)
            }

            val backImage = userDoc.getString("backImage").orEmpty()
            if (backImage.isNotBlank()) {
                Glide.with(requireContext()).load(backImage).into(binding.userBackgroundImage)
            } else {
                binding.userBackgroundImage.setBackgroundResource(R.color.secondary)
            }
        })
    }

    private fun observePosts() {
        postsViewModel.liveData3.observe(viewLifecycleOwner, Observer { posts ->
            val safePosts = posts ?: arrayListOf()
            binding.userPostsCountUserProfileFrag.text = "Posts: ${safePosts.size}"
            binding.userProfilePostsRecycler.adapter =
                PostListUserProfileAdapter(requireContext(), safePosts, this)
        })
    }

    private fun startEditingProfile() {
        binding.imageEdit.visibility = View.GONE
        binding.imageChecked.visibility = View.VISIBLE

        binding.cityEditUserProfile.setText(binding.userCityUserProfileFrag.text.toString().removePrefix("City: ").trim())
        binding.cityEditUserProfile.visibility = View.VISIBLE

        binding.aboutValueEditUserProfileFrag.setText(binding.aboutValueUserProfileFrag.text.toString())
        binding.aboutValueEditUserProfileFrag.visibility = View.VISIBLE
        binding.aboutValueUserProfileFrag.visibility = View.GONE
    }

    private fun saveProfileEdits() {
        val user = firebaseAuth.currentUser ?: return

        binding.imageEdit.visibility = View.VISIBLE
        binding.imageChecked.visibility = View.GONE
        binding.cityEditUserProfile.visibility = View.GONE
        binding.aboutValueEditUserProfileFrag.visibility = View.GONE
        binding.aboutValueUserProfileFrag.visibility = View.VISIBLE

        userDataViewModel.updateUserField(
            requireContext(),
            user.uid,
            binding.aboutValueEditUserProfileFrag.text.toString().trim(),
            binding.cityEditUserProfile.text.toString().trim()
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
