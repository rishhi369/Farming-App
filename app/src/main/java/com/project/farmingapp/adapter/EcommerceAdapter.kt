package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleEcommItemBinding
import com.project.farmingapp.utilities.CellClickListener

class EcommerceAdapter(
    val context: Context,
    val ecommtListData: List<DocumentSnapshot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<EcommerceAdapter.EcommercceViewHolder>() {

    class EcommercceViewHolder(val binding: SingleEcommItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EcommercceViewHolder {
        val binding = SingleEcommItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return EcommercceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ecommtListData.size
    }

    override fun onBindViewHolder(holder: EcommercceViewHolder, position: Int) {
        val currentList = ecommtListData[position]
        val binding = holder.binding

        binding.ecommtitle.text = currentList.get("title")?.toString().orEmpty()
        binding.ecommPrice.text = "\u20B9 " + currentList.get("price")?.toString().orEmpty()
        binding.ecommretailer.text = currentList.get("retailer")?.toString().orEmpty()
        binding.ecommItemAvailability.text = currentList.get("availability")?.toString().orEmpty()
        
        val allImages = currentList.get("imageUrl") as? List<String> ?: emptyList()
        Glide.with(context).load(allImages.firstOrNull()).into(binding.ecommImage)
        binding.ecommRating.rating = currentList.get("rating")?.toString()?.toFloatOrNull() ?: 0f

        binding.root.setOnClickListener {
            cellClickListener.onCellClickListener(currentList.id)
        }
    }
}
