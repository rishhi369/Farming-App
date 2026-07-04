package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.adapter.CartItemsAdapter
import com.project.farmingapp.databinding.FragmentCartBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.viewmodel.EcommViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CartFragment : Fragment(), CartItemBuy {

    private var param1: String? = null
    private var param2: String? = null
    var isOpened: Boolean = false
    var totalCount = 0
    var totalPrice = 0
    var items = HashMap<String, Any>()
    lateinit var ecommViewModel: EcommViewModel

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        ecommViewModel = EcommViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            context?.let {
                Toast.makeText(it, "Please login again.", Toast.LENGTH_SHORT).show()
            }
            binding.progressCart.visibility = View.GONE
            binding.loadingTitleText.visibility = View.GONE
            return
        }

        (activity as AppCompatActivity).supportActionBar?.title = "Cart"
        isOpened = true

        firebaseFirestore.collection("users").document(userId).collection("cart")
            .addSnapshotListener { snapshot, error ->
                if (_binding == null) return@addSnapshotListener
                
                if (error != null) {
                    context?.let {
                        Toast.makeText(it, error.message ?: "Failed to load cart", Toast.LENGTH_SHORT).show()
                    }
                    binding.progressCart.visibility = View.GONE
                    binding.loadingTitleText.visibility = View.GONE
                    return@addSnapshotListener
                }

                val docs = snapshot?.documents.orEmpty()
                items = HashMap()

                docs.forEach { doc ->
                    items[doc.id] = hashMapOf(
                        "quantity" to (doc.getLong("quantity")?.toInt() ?: 1),
                        "time" to (doc.getString("time") ?: "")
                    )
                }

                var totalCartPrice = 0
                if (items.isEmpty()) {
                    binding.totalItemsValue.text = "0"
                    binding.totalCostValue.text = "\u20B90"
                }

                for ((key, value) in items) {
                    val currVal = value as Map<String, Any>
                    ecommViewModel.getSpecificItem(key).observe(viewLifecycleOwner, Observer {
                        if (_binding == null) return@Observer
                        val quantity = currVal["quantity"].toString().toIntOrNull() ?: 1
                        val itemPrice = it.get("price").toString().toIntOrNull() ?: 0
                        val deliveryCharge = it.get("delCharge").toString().toIntOrNull() ?: 0
                        totalCartPrice += quantity * itemPrice + deliveryCharge
                        binding.totalItemsValue.text = items.size.toString()
                        binding.totalCostValue.text = "\u20B9" + totalCartPrice.toString()
                    })
                }

                val adapter = CartItemsAdapter(this@CartFragment, items, this@CartFragment)
                binding.recyclerCart.adapter = adapter
                binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
                binding.progressCart.visibility = View.GONE
                binding.loadingTitleText.visibility = View.GONE
            }

        binding.buyAllBtn.setOnClickListener {
            // Future implementation
        }
    }

    override fun addToOrders(productId: String, quantity: Int, itemCost: Int, deliveryCost: Int) {
        val hostContext = context ?: return
        Intent(hostContext, RazorPayActivity::class.java).also {
            it.putExtra("productId", productId)
            it.putExtra("itemCost", itemCost.toString())
            it.putExtra("quantity", quantity.toString())
            it.putExtra("deliveryCost", deliveryCost.toString())
            startActivity(it)
        }
    }
}
