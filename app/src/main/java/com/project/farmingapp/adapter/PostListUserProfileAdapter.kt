package com.project.farmingapp.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.R
import com.project.farmingapp.databinding.UserProfilePostsSingleBinding
import com.project.farmingapp.utilities.CellClickListener

class PostListUserProfileAdapter(
    val context: Context,
    var listData: ArrayList<DocumentSnapshot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<PostListUserProfileAdapter.PostListUserProfileViewHolder>() {

    class PostListUserProfileViewHolder(val binding: UserProfilePostsSingleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostListUserProfileViewHolder {
        val binding = UserProfilePostsSingleBinding.inflate(LayoutInflater.from(context), parent, false)
        return PostListUserProfileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: PostListUserProfileViewHolder, position: Int) {
        val currentData = listData[position]
        val binding = holder.binding

        binding.userPostTitleUserProfileFrag.text =
            currentData.getString("title").orEmpty().ifBlank { "Farm Update" }
        binding.userPostUploadTimeUserProfileFrag.text =
            DateUtils.getRelativeTimeSpanString(currentData.getLong("timeStamp") ?: System.currentTimeMillis()).toString()
        
        binding.userPostProfileCard.setOnClickListener {
            cellClickListener.onCellClickListener(currentData.id)
        }
        
        val imageUrl = currentData.getString("imageUrl").orEmpty()
        if (imageUrl.isNotBlank()) {
            Glide.with(context).load(imageUrl).into(binding.userPostImageUserProfileFrag)
        } else {
            binding.userPostImageUserProfileFrag.setImageResource(R.drawable.ic_user_profile)
        }
    }
}
