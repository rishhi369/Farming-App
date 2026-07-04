package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleDashboardEcommItemBinding
import com.project.farmingapp.utilities.CellClickListener

class DashboardEcomItemAdapter(
    var context: Context,
    val allData: List<DocumentSnapshot>,
    val itemsToShow: List<Int>,
    val cellClickListener: CellClickListener
) : RecyclerView.Adapter<DashboardEcomItemAdapter.DashboardEcomItemViewHolder>() {

    class DashboardEcomItemViewHolder(val binding: SingleDashboardEcommItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardEcomItemViewHolder {
        val binding = SingleDashboardEcommItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DashboardEcomItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemsToShow.size
    }

    override fun onBindViewHolder(holder: DashboardEcomItemViewHolder, position: Int) {
        val currentData = allData[itemsToShow[position]]
        val binding = holder.binding

        binding.itemTitle.text = currentData.get("title")?.toString().orEmpty()
        binding.itemPrice.text = "\u20B9" + currentData.get("price")?.toString().orEmpty()
        
        val allImages = currentData.get("imageUrl") as? List<String> ?: emptyList()
        Glide.with(context).load(allImages.firstOrNull()).into(binding.itemImage)
        
        binding.root.setOnClickListener {
            cellClickListener.onCellClickListener(currentData.id)
        }
    }
}
