package com.project.farmingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleMyorderItemBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.view.ecommerce.MyOrdersFragment
import com.project.farmingapp.viewmodel.EcommViewModel

class MyOrdersAdapter(
    private val context: MyOrdersFragment,
    private val allData: List<DocumentSnapshot>,
    private val cellClickListener: CellClickListener,
    private val cartItemBuy: CartItemBuy
) : RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>() {

    class MyOrdersViewHolder(val binding: SingleMyorderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val binding = SingleMyorderItemBinding.inflate(LayoutInflater.from(context.requireContext()), parent, false)
        return MyOrdersViewHolder(binding)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        val order = allData[position]
        val binding = holder.binding
        val productId = order.getString("productId").orEmpty()
        val quantity = order.getLong("quantity")?.toInt() ?: 1
        val itemCost = order.getLong("itemCost")?.toInt() ?: 0
        val deliveryCost = order.getLong("deliveryCost")?.toInt() ?: 0
        val totalPrice = order.getLong("totalPrice")?.toInt() ?: (quantity * itemCost + deliveryCost)

        binding.myOrderItemName.text = "Loading product..."
        binding.myOrderItemPrice.text = "Rs $totalPrice"
        binding.myOrderPinCode.text = "Pin Code: ${order.getString("pincode").orEmpty()}"
        binding.myOderDeliveryCharge.text = deliveryCost.toString()
        binding.myOrderDeliveryStatus.text =
            order.getString("deliveryStatus").orEmpty().ifBlank { "Order placed" }
        binding.myOrderTimeStamp.text = order.getString("time").orEmpty().substringBefore(" ")
        binding.myOderItemImage.setImageResource(R.color.secondary)

        if (productId.isNotBlank()) {
            EcommViewModel().getSpecificItem(productId).observe(context) { product ->
                binding.myOrderItemName.text =
                    product?.getString("title").orEmpty().ifBlank { "Product" }

                val images = product?.get("imageUrl") as? List<*>
                val firstImage = images?.firstOrNull() as? String
                if (!firstImage.isNullOrBlank()) {
                    Glide.with(context).load(firstImage).into(binding.myOderItemImage)
                }
            }
        } else {
            binding.myOrderItemName.text = "Product"
        }

        binding.myOrderPurchaseAgain.setOnClickListener {
            cartItemBuy.addToOrders(productId, quantity, itemCost, deliveryCost)
        }

        binding.root.setOnClickListener {
            if (productId.isNotBlank()) {
                cellClickListener.onCellClickListener(productId)
            }
        }
    }
}
