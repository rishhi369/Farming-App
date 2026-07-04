package com.project.farmingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.R
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.view.ecommerce.MyOrdersFragment
import com.project.farmingapp.viewmodel.EcommViewModel
import kotlinx.android.synthetic.main.single_myorder_item.view.*

class MyOrdersAdapter(
    private val context: MyOrdersFragment,
    private val allData: List<DocumentSnapshot>,
    private val cellClickListener: CellClickListener,
    private val cartItemBuy: CartItemBuy
) : RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>() {

    class MyOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val view = LayoutInflater.from(context.requireContext())
            .inflate(R.layout.single_myorder_item, parent, false)
        return MyOrdersViewHolder(view)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        val order = allData[position]
        val productId = order.getString("productId").orEmpty()
        val quantity = order.getLong("quantity")?.toInt() ?: 1
        val itemCost = order.getLong("itemCost")?.toInt() ?: 0
        val deliveryCost = order.getLong("deliveryCost")?.toInt() ?: 0
        val totalPrice = order.getLong("totalPrice")?.toInt() ?: (quantity * itemCost + deliveryCost)

        holder.itemView.myOrderItemName.text = "Loading product..."
        holder.itemView.myOrderItemPrice.text = "Rs $totalPrice"
        holder.itemView.myOrderPinCode.text = "Pin Code: ${order.getString("pincode").orEmpty()}"
        holder.itemView.myOderDeliveryCharge.text = deliveryCost.toString()
        holder.itemView.myOrderDeliveryStatus.text =
            order.getString("deliveryStatus").orEmpty().ifBlank { "Order placed" }
        holder.itemView.myOrderTimeStamp.text = order.getString("time").orEmpty().substringBefore(" ")
        holder.itemView.myOderItemImage.setImageResource(R.color.secondary)

        if (productId.isNotBlank()) {
            EcommViewModel().getSpecificItem(productId).observe(context) { product ->
                holder.itemView.myOrderItemName.text =
                    product?.getString("title").orEmpty().ifBlank { "Product" }

                val images = product?.get("imageUrl") as? List<*>
                val firstImage = images?.firstOrNull() as? String
                if (!firstImage.isNullOrBlank()) {
                    Glide.with(context).load(firstImage).into(holder.itemView.myOderItemImage)
                }
            }
        } else {
            holder.itemView.myOrderItemName.text = "Product"
        }

        holder.itemView.myOrderPurchaseAgain.setOnClickListener {
            cartItemBuy.addToOrders(productId, quantity, itemCost, deliveryCost)
        }

        holder.itemView.setOnClickListener {
            if (productId.isNotBlank()) {
                cellClickListener.onCellClickListener(productId)
            }
        }
    }
}
