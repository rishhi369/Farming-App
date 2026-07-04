package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.databinding.SingleNormalAttributesEcommBinding

class AttributesNormalAdapter(val context: Context, val allData: List<Map<String, Any>>) :
    RecyclerView.Adapter<AttributesNormalAdapter.AttributesNormalViewHolder>() {

    class AttributesNormalViewHolder(val binding: SingleNormalAttributesEcommBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributesNormalViewHolder {
        val binding = SingleNormalAttributesEcommBinding.inflate(LayoutInflater.from(context), parent, false)
        return AttributesNormalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: AttributesNormalViewHolder, position: Int) {
        val currentData = allData[position]
        val binding = holder.binding
        
        for ((key, value) in currentData) {
            binding.normalAttributeTitle.text = key + " - "
            binding.normalAttributeValue.text = value.toString()
        }
    }
}