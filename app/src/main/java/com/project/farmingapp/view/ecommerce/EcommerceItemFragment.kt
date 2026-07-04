package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.adapter.AttributesNormalAdapter
import com.project.farmingapp.adapter.AttributesSelectionAdapter
import com.project.farmingapp.adapter.EcommImageSliderAdapter
import com.project.farmingapp.databinding.FragmentEcommerceItemBinding
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.EcommViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EcommerceItemFragment : Fragment(), CellClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewmodel: EcommViewModel
    private var currentItemId: String? = null
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault())

    private var _binding: FragmentEcommerceItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewmodel = ViewModelProvider(requireActivity())
            .get(EcommViewModel::class.java)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEcommerceItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "E-Commerce"

        setupQuantityButtons()
        setupColorButtons()
        loadProductDetails()
        setupCartActions()
    }

    private fun setupQuantityButtons() {
        binding.increaseQtyBtn.setOnClickListener {
            binding.quantityCountEcomm.text = (binding.quantityCountEcomm.text.toString().toInt() + 1).toString()
        }

        binding.decreaseQtyBtn.setOnClickListener {
            if (binding.quantityCountEcomm.text.toString().toInt() != 1) {
                binding.quantityCountEcomm.text = (binding.quantityCountEcomm.text.toString().toInt() - 1).toString()
            }
        }
    }

    private fun setupColorButtons() {
        val color1Params = binding.color1.layoutParams
        val color2Params = binding.color2.layoutParams
        val color3Params = binding.color3.layoutParams
        val color4Params = binding.color4.layoutParams
        val density = resources.displayMetrics.density

        fun select(one: Boolean, two: Boolean, three: Boolean, four: Boolean) {
            color1Params.width = (density * if (one) 40 else 30).toInt()
            color1Params.height = (density * if (one) 35 else 25).toInt()
            color2Params.width = (density * if (two) 40 else 30).toInt()
            color2Params.height = (density * if (two) 35 else 25).toInt()
            color3Params.width = (density * if (three) 40 else 30).toInt()
            color3Params.height = (density * if (three) 35 else 25).toInt()
            color4Params.width = (density * if (four) 40 else 30).toInt()
            color4Params.height = (density * if (four) 35 else 25).toInt()
            binding.color1.layoutParams = color1Params
            binding.color2.layoutParams = color2Params
            binding.color3.layoutParams = color3Params
            binding.color4.layoutParams = color4Params
        }

        select(one = true, two = false, three = false, four = false)
        binding.color1.setOnClickListener { select(one = true, two = false, three = false, four = false) }
        binding.color2.setOnClickListener { select(one = false, two = true, three = false, four = false) }
        binding.color3.setOnClickListener { select(one = false, two = false, three = true, four = false) }
        binding.color4.setOnClickListener { select(one = false, two = false, three = false, four = true) }
    }

    private fun loadProductDetails() {
        binding.loadingText.text = "Loading..."
        val itemId = tag
        val cachedItem = viewmodel.ecommLiveData.value?.firstOrNull { it.id == itemId }
        if (cachedItem != null) {
            bindProduct(cachedItem)
            return
        }

        if (itemId.isNullOrBlank()) {
            binding.loadingText.text = "Product not found"
            binding.progressEcommItem.visibility = View.GONE
            return
        }

        viewmodel.getSpecificItem(itemId).observe(viewLifecycleOwner) {
            bindProduct(it)
        }
    }

    private fun bindProduct(product: DocumentSnapshot) {
        currentItemId = product.id
        binding.productTitle.text = product.getString("title") ?: "Product"
        binding.productShortDescription.text = product.getString("shortDesc") ?: ""
        binding.productPrice.text = "\u20B9" + (product.get("price")?.toString() ?: "0")
        binding.productLongDesc.text = product.getString("longDesc") ?: "-"
        binding.howToUseText.text = product.getString("howtouse") ?: "-"
        binding.deliverycost.text = product.get("delCharge")?.toString() ?: "0"
        binding.Rating.rating = product.get("rating").toString().toFloatOrNull() ?: 0f

        val attributes = product.get("attributes") as? Map<String, Any> ?: emptyMap()
        binding.colorLinear.visibility = if (attributes.containsKey("Color")) View.VISIBLE else View.GONE
        binding.colorTitle.visibility = binding.colorLinear.visibility

        val selectionAttributes = mutableListOf<MutableMap<String, Any>>()
        val normalAttributes = mutableListOf<MutableMap<String, Any>>()
        for ((key, value) in attributes) {
            if (value is ArrayList<*> && key != "Color") selectionAttributes.add(mutableMapOf(key to value))
            if (value is String) normalAttributes.add(mutableMapOf(key to value))
        }

        binding.recyclerSelectionAttributes.adapter =
            AttributesSelectionAdapter(requireContext(), selectionAttributes, this)
        binding.recyclerSelectionAttributes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNormalAttributes.adapter =
            AttributesNormalAdapter(requireContext(), normalAttributes)
        binding.recyclerNormalAttributes.layoutManager = LinearLayoutManager(requireContext())

        val images = product.get("imageUrl") as? List<String> ?: emptyList()
        binding.posterSlider.adapter = EcommImageSliderAdapter(images)
        binding.progressEcommItem.visibility = View.GONE
        binding.loadingText.visibility = View.GONE
    }

    private fun setupCartActions() {
        binding.addToCart.setOnClickListener {
            val itemId = currentItemId
            val userId = firebaseAuth.currentUser?.uid
            if (itemId.isNullOrEmpty() || userId.isNullOrEmpty()) {
                context?.let {
                    Toast.makeText(it, "Product is still loading. Please try again.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            binding.addToCart.isClickable = false
            binding.progressEcommItem.visibility = View.VISIBLE
            binding.loadingText.text = "Adding to Cart..."
            binding.loadingText.visibility = View.VISIBLE

            val cartData = hashMapOf(
                "quantity" to binding.quantityCountEcomm.text.toString().toInt(),
                "time" to sdf.format(Date())
            )

            firebaseFirestore.collection("users").document(userId)
                .collection("cart").document(itemId)
                .set(cartData)
                .addOnSuccessListener {
                    context?.let {
                        Toast.makeText(it, "Item Added", Toast.LENGTH_SHORT).show()
                    }
                    if (_binding != null) {
                        binding.progressEcommItem.visibility = View.GONE
                        binding.loadingText.visibility = View.GONE
                        binding.addToCart.isClickable = true
                    }
                }
                .addOnFailureListener { exception ->
                    context?.let {
                        Toast.makeText(it, exception.message ?: "Please Try Again!", Toast.LENGTH_SHORT).show()
                    }
                    if (_binding != null) {
                        binding.progressEcommItem.visibility = View.GONE
                        binding.loadingText.visibility = View.GONE
                        binding.addToCart.isClickable = true
                    }
                }
        }

        binding.buynow.setOnClickListener {
            val itemCost = binding.productPrice.text.toString().filter { it.isDigit() }
            val hostContext = context ?: return@setOnClickListener
            Intent(hostContext, RazorPayActivity::class.java).also {
                it.putExtra("productId", currentItemId.toString())
                it.putExtra("itemCost", itemCost)
                it.putExtra("quantity", binding.quantityCountEcomm.text.toString())
                it.putExtra("deliveryCost", binding.deliverycost.text.toString())
                startActivity(it)
            }
        }
    }

    override fun onCellClickListener(name: String) {
        val selectionAttributeAllData = name.split(" ")
        Log.d("EcommItem", selectionAttributeAllData.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cart_item) {
            val cartFragment = CartFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, cartFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("cart")
                .commit()
        }
        return super.onOptionsItemSelected(item)
    }
}
