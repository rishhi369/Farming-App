package com.project.farmingapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.view.ecommerce.CartFragment
import com.project.farmingapp.viewmodel.EcommViewModel
import kotlinx.android.synthetic.main.single_cart_item.view.*

class CartItemsAdapter(
    val context: CartFragment,
    val allData: HashMap<String, Any>,
    val cartitembuy: CartItemBuy
) : RecyclerView.Adapter<CartItemsAdapter.CartItemsViewHolder>() {

    var itemCost = 0
    var deliveryCharge = 0
    var quantity = 0

    class CartItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsViewHolder {
        val view = LayoutInflater.from(context.context).inflate(R.layout.single_cart_item, parent, false)
        return CartItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: CartItemsViewHolder, position: Int) {
        val currentData = allData.entries.toTypedArray()[position]
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid ?: return
        val itemRef = firebaseFirestore.collection("users").document(userId)
            .collection("cart").document(currentData.key)
        val curr = currentData.value as Map<String, Any>

        holder.itemView.cartItemBuyBtn.setOnClickListener {
            val qty = holder.itemView.quantityCountEcomm.text.toString().toInt()
            val itemPrice = holder.itemView.itemPriceCart.text.toString()
                .filter { it.isDigit() }
                .toIntOrNull() ?: 0
            val deliveryCharge = holder.itemView.deliveryChargeCart.text.toString().toInt()
            Log.d("totalPrice", quantity.toString())
            Log.d("totalPrice", itemCost.toString())
            Log.d("totalPrice", deliveryCharge.toString())
            cartitembuy.addToOrders(currentData.key, qty, itemPrice, deliveryCharge)
        }

        holder.itemView.removeCartBtn.setOnClickListener {
            itemRef.delete()
        }

        holder.itemView.increaseQtyBtn.setOnClickListener {
            holder.itemView.quantityCountEcomm.text =
                (holder.itemView.quantityCountEcomm.text.toString().toInt() + 1).toString()
            itemRef.update("quantity", holder.itemView.quantityCountEcomm.text.toString().toInt())
        }

        holder.itemView.decreaseQtyBtn.setOnClickListener {
            if (holder.itemView.quantityCountEcomm.text.toString().toInt() != 1) {
                holder.itemView.quantityCountEcomm.text =
                    (holder.itemView.quantityCountEcomm.text.toString().toInt() - 1).toString()
                itemRef.update("quantity", holder.itemView.quantityCountEcomm.text.toString().toInt())
            }
        }

        val ecommViewModel = EcommViewModel()

        ecommViewModel.getSpecificItem(currentData.key).observe(context, Observer {
            itemCost = it.get("price").toString().toInt()
            deliveryCharge = it.get("delCharge").toString().toInt()
            quantity = curr["quantity"].toString().toInt()
            holder.itemView.itemNameCart.text = it.getString("title").toString()
            holder.itemView.itemPriceCart.text = "\u20B9" + itemCost.toString()
            holder.itemView.quantityCountEcomm.text = quantity.toString()
            holder.itemView.deliveryChargeCart.text = deliveryCharge.toString()
            holder.itemView.cartItemFirm.text = it.get("retailer").toString()
            holder.itemView.cartItemAvailability.text = it.get("availability").toString()

            val allImages = it.get("imageUrl") as? List<String> ?: emptyList()
            Glide.with(context).load(allImages.firstOrNull()).into(holder.itemView.cartItemImage)
            holder.itemView.cartItemBuyBtn.text =
                "Buy Now: " + "\u20B9" + (itemCost * quantity + deliveryCharge).toString()
        })
    }
}
