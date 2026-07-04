package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.farmingapp.R
import com.project.farmingapp.adapter.MyOrdersAdapter
import com.project.farmingapp.databinding.FragmentMyOrdersBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.utilities.CellClickListener

class MyOrdersFragment : Fragment(), CellClickListener, CartItemBuy {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "My Orders"
        binding.myOrderRecycler.layoutManager = LinearLayoutManager(requireContext())
        loadOrders()
    }

    private fun loadOrders() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            showEmpty("Please login again to view orders")
            return
        }

        firebaseFirestore.collection("users")
            .document(user.uid)
            .collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (_binding == null) return@addSnapshotListener
                
                if (error != null) {
                    showEmpty(error.message ?: "Unable to load orders")
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents.orEmpty()
                if (orders.isEmpty()) {
                    showEmpty("No orders yet. Add a product and place a demo order.")
                } else {
                    binding.emptyOrdersText.visibility = View.GONE
                    binding.myOrderRecycler.visibility = View.VISIBLE
                    binding.myOrderRecycler.adapter =
                        MyOrdersAdapter(this, orders, this@MyOrdersFragment, this@MyOrdersFragment)
                }
            }
    }

    private fun showEmpty(message: String) {
        if (_binding == null) return
        binding.emptyOrdersText.text = message
        binding.emptyOrdersText.visibility = View.VISIBLE
        binding.myOrderRecycler.visibility = View.GONE
    }

    override fun onCellClickListener(name: String) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, EcommerceItemFragment(), name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("ecommItem")
            .commit()
    }

    override fun addToOrders(productId: String, quantity: Int, itemCost: Int, deliveryCost: Int) {
        Intent(requireContext(), RazorPayActivity::class.java).also {
            it.putExtra("productId", productId)
            it.putExtra("itemCost", itemCost.toString())
            it.putExtra("quantity", quantity.toString())
            it.putExtra("deliveryCost", deliveryCost.toString())
            startActivity(it)
        }
    }
}
