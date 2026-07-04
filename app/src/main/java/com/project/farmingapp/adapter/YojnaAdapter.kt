package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.databinding.SingleYojnaListBinding
import com.project.farmingapp.utilities.CellClickListener

class YojnaAdapter(
    val context: Context,
    val yojnaData: List<DocumentSnapshot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<YojnaAdapter.YojnaListviewHolder>() {

    class YojnaListviewHolder(val binding: SingleYojnaListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YojnaListviewHolder {
        val binding = SingleYojnaListBinding.inflate(LayoutInflater.from(context), parent, false)
        return YojnaListviewHolder(binding)
    }

    override fun getItemCount(): Int {
        return yojnaData.size
    }

    override fun onBindViewHolder(holder: YojnaListviewHolder, position: Int) {
        val singleYojna = yojnaData[position]
        val binding = holder.binding

        binding.yojnaTitleYojnaList.text = singleYojna.getString("title") ?: "Scheme"
        binding.yojnaStatusYojnaList.text = singleYojna.getString("status") ?: "-"
        binding.yojnaDateYojnaList.text = singleYojna.get("launch")?.toString() ?: "-"
        
        val url = singleYojna.getString("image")
        Glide.with(context)
            .load(url)
            .into(binding.yojnaImageYojnaList)

        binding.singlelistyojnacard.setOnClickListener {
            cellClickListener.onCellClickListener(singleYojna.id)
        }
    }
}
