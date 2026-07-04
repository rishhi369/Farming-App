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
import com.project.farmingapp.databinding.SingleCartItemBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.view.ecommerce.CartFragment
import com.project.farmingapp.viewmodel.EcommViewModel

class CartItemsAdapter(
    val context: CartFragment,
    val allData: HashMap<String, Any>,
    val cartitembuy: CartItemBuy
) : RecyclerView.Adapter<CartItemsAdapter.CartItemsViewHolder>() {

    var itemCost = 0
    var deliveryCharge = 0
    var quantity = 0

    class CartItemsViewHolder(val binding: SingleCartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsViewHolder {
        val binding = SingleCartItemBinding.inflate(LayoutInflater.from(context.requireContext()), parent, false)
        return CartItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: CartItemsViewHolder, position: Int) {
        val currentData = allData.entries.toTypedArray()[position]
        val binding = holder.binding
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid ?: return
        val itemRef = firebaseFirestore.collection("users").document(userId)
            .collection("cart").document(currentData.key)
        val curr = currentData.value as Map<String, Any>

        binding.cartItemBuyBtn.setOnClickListener {
            val qty = binding.quantityCountEcomm.text.toString().toInt()
            val itemPrice = binding.itemPriceCart.text.toString()
                .filter { it.isDigit() }
                .toIntOrNull() ?: 0
            val deliveryChargeVal = binding.deliveryChargeCart.text.toString().toIntOrNull() ?: 0
            cartitembuy.addToOrders(currentData.key, qty, itemPrice, deliveryChargeVal)
        }

        binding.removeCartBtn.setOnClickListener {
            itemRef.delete()
        }

        binding.increaseQtyBtn.setOnClickListener {
            val newQty = binding.quantityCountEcomm.text.toString().toInt() + 1
            binding.quantityCountEcomm.text = newQty.toString()
            itemRef.update("quantity", newQty)
        }

        binding.decreaseQtyBtn.setOnClickListener {
            val currentQty = binding.quantityCountEcomm.text.toString().toInt()
            if (currentQty > 1) {
                val newQty = currentQty - 1
                binding.quantityCountEcomm.text = newQty.toString()
                itemRef.update("quantity", newQty)
            }
        }

        val ecommViewModel = EcommViewModel()

        ecommViewModel.getSpecificItem(currentData.key).observe(context, Observer {
            if (it == null) return@Observer
            
            itemCost = it.get("price")?.toString()?.toIntOrNull() ?: 0
            deliveryCharge = it.get("delCharge")?.toString()?.toIntOrNull() ?: 0
            quantity = curr["quantity"]?.toString()?.toIntOrNull() ?: 1
            
            binding.itemNameCart.text = it.getString("title").orEmpty()
            binding.itemPriceCart.text = "\u20B9" + itemCost.toString()
            binding.quantityCountEcomm.text = quantity.toString()
            binding.deliveryChargeCart.text = deliveryCharge.toString()
            binding.cartItemFirm.text = it.get("retailer")?.toString().orEmpty()
            binding.cartItemAvailability.text = it.get("availability")?.toString().orEmpty()

            val allImages = it.get("imageUrl") as? List<String> ?: emptyList()
            Glide.with(context).load(allImages.firstOrNull()).into(binding.cartItemImage)
            binding.cartItemBuyBtn.text =
                "Buy Now: " + "\u20B9" + (itemCost * quantity + deliveryCharge).toString()
        })
    }
}
