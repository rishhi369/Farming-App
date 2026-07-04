package com.project.farmingapp.view.socialmedia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.farmingapp.R
import com.project.farmingapp.adapter.SMPostListAdapter
import com.project.farmingapp.databinding.FragmentSocialMediaPostsBinding

class SocialMediaPostsFragment : Fragment() {

    private var adapter: SMPostListAdapter? = null
    private var _binding: FragmentSocialMediaPostsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSocialMediaPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Social Media"

        binding.postsRecycler.layoutManager = LinearLayoutManager(requireContext())
        loadPosts()

        binding.createPostFloating.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, SMCreatePostFragment(), "smCreate")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("smCreate")
                .commit()
        }
    }

    private fun loadPosts() {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (_binding == null) return@addSnapshotListener
                
                if (error != null) {
                    Log.e("SocialMediaPosts", "Unable to load posts", error)
                    binding.emptyPostsText.visibility = View.VISIBLE
                    binding.emptyPostsText.text = "Unable to load posts right now"
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents.orEmpty()
                binding.emptyPostsText.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
                adapter = SMPostListAdapter(requireContext(), posts)
                binding.postsRecycler.adapter = adapter
            }
    }
}
