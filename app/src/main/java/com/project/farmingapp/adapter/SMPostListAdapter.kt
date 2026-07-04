package com.project.farmingapp.adapter

import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.databinding.PostWithImageSmBinding

class SMPostListAdapter(
    private val context: Context,
    private val postListData: List<DocumentSnapshot>
) : RecyclerView.Adapter<SMPostListAdapter.SMPostListViewHolder>() {

    class SMPostListViewHolder(val binding: PostWithImageSmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMPostListViewHolder {
        val binding = PostWithImageSmBinding.inflate(LayoutInflater.from(context), parent, false)
        return SMPostListViewHolder(binding)
    }

    override fun getItemCount(): Int = postListData.size

    override fun onBindViewHolder(holder: SMPostListViewHolder, position: Int) {
        val currentPost = postListData[position]
        val binding = holder.binding

        binding.userNamePostSM.text = currentPost.getString("name").orEmpty().ifBlank { "Farmer" }
        binding.userPostTitleValue.text = currentPost.getString("title").orEmpty().ifBlank { "Farm Update" }
        binding.userPostDescValue.text = currentPost.getString("description").orEmpty()
        binding.userPostCategorySM.text =
            currentPost.getString("category").orEmpty().ifBlank { "Farming Tip" }

        val timestamp = currentPost.getLong("timeStamp") ?: System.currentTimeMillis()
        binding.userPostUploadTime.text = DateUtils.getRelativeTimeSpanString(timestamp)
        bindLikes(binding, currentPost)
        bindComments(binding, currentPost)

        binding.postImageSM.visibility = View.GONE
        binding.postVideoSM.visibility = View.GONE

        when (currentPost.getString("uploadType").orEmpty()) {
            "video" -> showVideo(binding, currentPost.getString("imageUrl").orEmpty())
            "image" -> showImage(binding, currentPost.getString("imageUrl").orEmpty())
        }

        binding.userProfileImageCard.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        binding.postContainer.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)

        binding.userPostDescValue.setOnClickListener {
            binding.userPostDescValue.maxLines = Int.MAX_VALUE
        }

        val userId = currentPost.getString("userID").orEmpty()
        if (userId.isNotBlank()) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener {
                    val profileImage = it.getString("profileImage").orEmpty()
                    if (profileImage.isNotBlank()) {
                        Glide.with(context).load(profileImage).into(binding.userProfileImagePost)
                    } else {
                        binding.userProfileImagePost.setImageResource(R.drawable.ic_user_profile)
                    }
                }
        } else {
            binding.userProfileImagePost.setImageResource(R.drawable.ic_user_profile)
        }
    }

    private fun bindComments(binding: PostWithImageSmBinding, currentPost: DocumentSnapshot) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val commentsCount = currentPost.getLong("commentsCount") ?: 0L
        binding.postCommentsCountSM.text = "$commentsCount ${if (commentsCount == 1L) "comment" else "comments"}"

        binding.postCommentSendBtnSM.setOnClickListener {
            val user = currentUser
            val commentText = binding.postCommentInputSM.text.toString().trim()

            if (user == null || commentText.isBlank()) {
                if (commentText.isBlank()) {
                    Toast.makeText(context, "Enter a comment first", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            binding.postCommentSendBtnSM.isEnabled = false
            val db = FirebaseFirestore.getInstance()
            val postRef = db.collection("posts").document(currentPost.id)

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { userDoc ->
                    val userName = user.displayName
                        ?: userDoc.getString("name")
                        ?: user.email?.substringBefore("@")
                        ?: "Farmer"

                    val commentData = hashMapOf<String, Any>(
                        "userId" to user.uid,
                        "name" to userName,
                        "comment" to commentText,
                        "timeStamp" to System.currentTimeMillis()
                    )

                    postRef.collection("comments")
                        .add(commentData)
                        .addOnSuccessListener {
                            postRef.update("commentsCount", FieldValue.increment(1))
                            binding.postCommentInputSM.text?.clear()
                            binding.postCommentSendBtnSM.isEnabled = true
                            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            binding.postCommentSendBtnSM.isEnabled = true
                            Toast.makeText(context, "Unable to add comment", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    binding.postCommentSendBtnSM.isEnabled = true
                    Toast.makeText(context, "Unable to add comment", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun bindLikes(binding: PostWithImageSmBinding, currentPost: DocumentSnapshot) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val likedBy = currentPost.get("likedBy") as? List<*> ?: emptyList<Any>()
        val likes = currentPost.getLong("likes") ?: likedBy.size.toLong()
        val isLiked = currentUserId.isNotBlank() && likedBy.contains(currentUserId)

        binding.postLikesCountSM.text = "$likes ${if (likes == 1L) "like" else "likes"}"
        binding.likePostBtnSM.text = if (isLiked) "Liked" else "Like"

        binding.likePostBtnSM.setOnClickListener {
            if (currentUserId.isBlank()) return@setOnClickListener

            binding.likePostBtnSM.isEnabled = false
            val postRef = FirebaseFirestore.getInstance().collection("posts").document(currentPost.id)

            if (isLiked) {
                postRef.update(
                    mapOf(
                        "likes" to FieldValue.increment(-1),
                        "likedBy" to FieldValue.arrayRemove(currentUserId)
                    )
                ).addOnCompleteListener {
                    binding.likePostBtnSM.isEnabled = true
                }
            } else {
                postRef.update(
                    mapOf(
                        "likes" to FieldValue.increment(1),
                        "likedBy" to FieldValue.arrayUnion(currentUserId)
                    )
                ).addOnCompleteListener {
                    binding.likePostBtnSM.isEnabled = true
                }
            }
        }
    }

    private fun showImage(binding: PostWithImageSmBinding, imageUrl: String) {
        if (imageUrl.isBlank()) return

        Glide.with(context).load(imageUrl).into(binding.postImageSM)
        binding.postImageSM.visibility = View.VISIBLE
    }

    private fun showVideo(binding: PostWithImageSmBinding, videoUrl: String) {
        if (videoUrl.isBlank()) return

        val webSet: WebSettings = binding.postVideoSM.settings
        webSet.javaScriptEnabled = true
        webSet.loadWithOverviewMode = true
        webSet.useWideViewPort = true

        binding.postVideoSM.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) = Unit
        }
        binding.postVideoSM.loadUrl(videoUrl)
        binding.postVideoSM.visibility = View.VISIBLE
    }
}
