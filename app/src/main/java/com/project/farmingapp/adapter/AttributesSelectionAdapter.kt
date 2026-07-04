package com.project.farmingapp.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleSelectionAttributesEcommBinding
import com.project.farmingapp.utilities.CellClickListener

class AttributesSelectionAdapter(
    var context: Context,
    var allData: List<Map<String, Any>>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<AttributesSelectionAdapter.AttributesSelectionViewHolder>() {

    class AttributesSelectionViewHolder(val binding: SingleSelectionAttributesEcommBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttributesSelectionViewHolder {
        val binding = SingleSelectionAttributesEcommBinding.inflate(LayoutInflater.from(context), parent, false)
        return AttributesSelectionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: AttributesSelectionViewHolder, position: Int) {
        val currentData = allData[position]
        val binding = holder.binding

        for ((key, values) in currentData) {
            cellClickListener.onCellClickListener("1 $key")
            binding.attributeTitle.text = key
            
            val allValues = values as? List<String> ?: emptyList()
            
            val firstValue = allValues.getOrNull(0).orEmpty().split(" ")
            binding.attribute1.text = firstValue.getOrNull(0).orEmpty()
            binding.attribute1Price.text = firstValue.getOrNull(1).orEmpty()

            val secondValue = allValues.getOrNull(1).orEmpty().split(" ")
            binding.attribute2.text = secondValue.getOrNull(0).orEmpty()
            binding.attribute2Price.text = secondValue.getOrNull(1).orEmpty()

            val thirdValue = allValues.getOrNull(2).orEmpty().split(" ")
            binding.attribute3.text = thirdValue.getOrNull(0).orEmpty()
            binding.attribute3Price.text = thirdValue.getOrNull(1).orEmpty()

            binding.cardSize1.setOnClickListener {
                cellClickListener.onCellClickListener("1 $key")
                Toast.makeText(context, "You Clicked 1", Toast.LENGTH_SHORT).show()
                binding.cardSize1.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                binding.attribute1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.attribute1Price.setTextColor(Color.parseColor("#FFFFFF"))
                binding.attribute1.setTypeface(null, Typeface.BOLD)
                binding.attribute1Price.setTypeface(null, Typeface.BOLD)

                binding.cardSize2.backgroundTintList = ContextCompat.getColorStateList(context, R.color.secondary)
                binding.attribute2.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute2Price.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute2.setTypeface(null, Typeface.NORMAL)
                binding.attribute2Price.setTypeface(null, Typeface.NORMAL)

                binding.cardSize3.backgroundTintList = ContextCompat.getColorStateList(context, R.color.secondary)
                binding.attribute3.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute3Price.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute3.setTypeface(null, Typeface.NORMAL)
                binding.attribute3Price.setTypeface(null, Typeface.NORMAL)
            }

            binding.cardSize2.setOnClickListener {
                cellClickListener.onCellClickListener("2 $key")
                Toast.makeText(context, "You Clicked 2", Toast.LENGTH_SHORT).show()
                binding.cardSize2.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                binding.attribute2.setTextColor(Color.parseColor("#FFFFFF"))
                binding.attribute2Price.setTextColor(Color.parseColor("#FFFFFF"))
                binding.attribute2.setTypeface(null, Typeface.BOLD)
                binding.attribute2Price.setTypeface(null, Typeface.BOLD)

                binding.cardSize3.backgroundTintList = ContextCompat.getColorStateList(context, R.color.secondary)
                binding.attribute3.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute3Price.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute3.setTypeface(null, Typeface.NORMAL)
                binding.attribute3Price.setTypeface(null, Typeface.NORMAL)

                binding.cardSize1.backgroundTintList = ContextCompat.getColorStateList(context, R.color.secondary)
                binding.attribute1.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute1Price.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute1.setTypeface(null, Typeface.NORMAL)
                binding.attribute1Price.setTypeface(null, Typeface.NORMAL)
            }

            binding.cardSize3.setOnClickListener {
                cellClickListener.onCellClickListener("3 $key")
                Toast.makeText(context, "You Clicked 3", Toast.LENGTH_SHORT).show()
                binding.cardSize3.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                binding.attribute3.setTextColor(Color.parseColor("#FFFFFF"))
                binding.attribute3Price.setTextColor(Color.parseColor("#FFFFFF"))
                binding.attribute3.setTypeface(null, Typeface.BOLD)
                binding.attribute3Price.setTypeface(null, Typeface.BOLD)

                binding.cardSize1.backgroundTintList = ContextCompat.getColorStateList(context, R.color.secondary)
                binding.attribute1.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute1Price.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute1.setTypeface(null, Typeface.NORMAL)
                binding.attribute1Price.setTypeface(null, Typeface.NORMAL)

                binding.cardSize2.backgroundTintList = ContextCompat.getColorStateList(context, R.color.secondary)
                binding.attribute2.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute2Price.setTextColor(Color.parseColor("#FF404A3A"))
                binding.attribute2.setTypeface(null, Typeface.NORMAL)
                binding.attribute2Price.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}
