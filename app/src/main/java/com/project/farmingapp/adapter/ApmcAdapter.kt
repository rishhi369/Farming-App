package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.databinding.ApmcSingleListBinding
import com.project.farmingapp.model.data.APMCCustomRecords

class ApmcAdapter(val context: Context, val data: List<APMCCustomRecords>) :
    RecyclerView.Adapter<ApmcAdapter.ApmcViewHolder>() {

    class ApmcViewHolder(val binding: ApmcSingleListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApmcViewHolder {
        val binding = ApmcSingleListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ApmcViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ApmcViewHolder, position: Int) {
        val mainData = data[position]
        val binding = holder.binding

        binding.apmcNameValue.text = mainData.market.ifBlank { "Local Market" }
        binding.apmcLocationValue.text = "${mainData.district} , ${mainData.state}"
        binding.comodityname.text = mainData.commodity.joinToString("\n") { it.ifBlank { "Crop" } }
        binding.minvalue.text = mainData.min_price.joinToString("\n") { it.ifBlank { "-" } }
        binding.maxvalue.text = mainData.max_price.joinToString("\n") { it.ifBlank { "-" } }
    }
}
