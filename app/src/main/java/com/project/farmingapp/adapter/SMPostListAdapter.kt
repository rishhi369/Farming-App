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
import kotlinx.android.synthetic.main.post_with_image_sm.view.*

class SMPostListAdapter(
    private val context: Context,
    private val postListData: List<DocumentSnapshot>
) : RecyclerView.Adapter<SMPostListAdapter.SMPostListViewHolder>() {

    class SMPostListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMPostListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_with_image_sm, parent, false)
        return SMPostListViewHolder(view)
    }

    override fun getItemCount(): Int = postListData.size

    override fun onBindViewHolder(holder: SMPostListViewHolder, position: Int) {
        val currentPost = postListData[position]
        val item = holder.itemView

        item.userNamePostSM.text = currentPost.getString("name").orEmpty().ifBlank { "Farmer" }
        item.userPostTitleValue.text = currentPost.getString("title").orEmpty().ifBlank { "Farm Update" }
        item.userPostDescValue.text = currentPost.getString("description").orEmpty()
        item.userPostCategorySM.text =
            currentPost.getString("category").orEmpty().ifBlank { "Farming Tip" }

        val timestamp = currentPost.getLong("timeStamp") ?: System.currentTimeMillis()
        item.userPostUploadTime.text = DateUtils.getRelativeTimeSpanString(timestamp)
        bindLikes(item, currentPost)
        bindComments(item, currentPost)

        item.postImageSM.visibility = View.GONE
        item.postVideoSM.visibility = View.GONE

        when (currentPost.getString("uploadType").orEmpty()) {
            "video" -> showVideo(item, currentPost.getString("imageUrl").orEmpty())
            "image" -> showImage(item, currentPost.getString("imageUrl").orEmpty())
        }

        item.userProfileImageCard.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        item.post_container.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition)

        item.userPostDescValue.setOnClickListener {
            item.userPostDescValue.maxLines = Int.MAX_VALUE
        }

        val userId = currentPost.getString("userID").orEmpty()
        if (userId.isNotBlank()) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener {
                    val profileImage = it.getString("profileImage").orEmpty()
                    if (profileImage.isNotBlank()) {
                        Glide.with(context).load(profileImage).into(item.userProfileImagePost)
                    } else {
                        item.userProfileImagePost.setImageResource(R.drawable.ic_user_profile)
                    }
                }
        } else {
            item.userProfileImagePost.setImageResource(R.drawable.ic_user_profile)
        }
    }

    private fun bindComments(item: View, currentPost: DocumentSnapshot) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val commentsCount = currentPost.getLong("commentsCount") ?: 0L
        item.postCommentsCountSM.text = "$commentsCount ${if (commentsCount == 1L) "comment" else "comments"}"

        item.postCommentSendBtnSM.setOnClickListener {
            val user = currentUser
            val commentText = item.postCommentInputSM.text.toString().trim()

            if (user == null || commentText.isBlank()) {
                if (commentText.isBlank()) {
                    Toast.makeText(context, "Enter a comment first", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            item.postCommentSendBtnSM.isEnabled = false
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
                            item.postCommentInputSM.text?.clear()
                            item.postCommentSendBtnSM.isEnabled = true
                            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            item.postCommentSendBtnSM.isEnabled = true
                            Toast.makeText(context, "Unable to add comment", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    item.postCommentSendBtnSM.isEnabled = true
                    Toast.makeText(context, "Unable to add comment", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun bindLikes(item: View, currentPost: DocumentSnapshot) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val likedBy = currentPost.get("likedBy") as? List<*> ?: emptyList<Any>()
        val likes = currentPost.getLong("likes") ?: likedBy.size.toLong()
        val isLiked = currentUserId.isNotBlank() && likedBy.contains(currentUserId)

        item.postLikesCountSM.text = "$likes ${if (likes == 1L) "like" else "likes"}"
        item.likePostBtnSM.text = if (isLiked) "Liked" else "Like"

        item.likePostBtnSM.setOnClickListener {
            if (currentUserId.isBlank()) return@setOnClickListener

            item.likePostBtnSM.isEnabled = false
            val postRef = FirebaseFirestore.getInstance().collection("posts").document(currentPost.id)

            if (isLiked) {
                postRef.update(
                    mapOf(
                        "likes" to FieldValue.increment(-1),
                        "likedBy" to FieldValue.arrayRemove(currentUserId)
                    )
                ).addOnCompleteListener {
                    item.likePostBtnSM.isEnabled = true
                }
            } else {
                postRef.update(
                    mapOf(
                        "likes" to FieldValue.increment(1),
                        "likedBy" to FieldValue.arrayUnion(currentUserId)
                    )
                ).addOnCompleteListener {
                    item.likePostBtnSM.isEnabled = true
                }
            }
        }
    }

    private fun showImage(item: View, imageUrl: String) {
        if (imageUrl.isBlank()) return

        Glide.with(context).load(imageUrl).into(item.postImageSM)
        item.postImageSM.visibility = View.VISIBLE
    }

    private fun showVideo(item: View, videoUrl: String) {
        if (videoUrl.isBlank()) return

        val webSet: WebSettings = item.postVideoSM.settings
        webSet.javaScriptEnabled = true
        webSet.loadWithOverviewMode = true
        webSet.useWideViewPort = true

        item.postVideoSM.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) = Unit
        }
        item.postVideoSM.loadUrl(videoUrl)
        item.postVideoSM.visibility = View.VISIBLE
    }
}
