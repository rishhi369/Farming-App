package com.project.farmingapp.view.articles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.project.farmingapp.R
import com.project.farmingapp.adapter.ArticleListAdapter
import com.project.farmingapp.databinding.FragmentArticleListBinding
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.ArticleViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArticleListFragment : Fragment(), CellClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: ArticleViewModel
    lateinit var Adapter: ArticleListAdapter
    lateinit var fruitFragment: FruitsFragment
    private var collectionName: String = "article_fruits"
    private var screenTitle: String = "Articles"

    private var _binding: FragmentArticleListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            collectionName = it.getString("collectionName") ?: collectionName
            screenTitle = it.getString("title") ?: screenTitle
        }
        viewModel = ViewModelProvider(requireActivity())
            .get(ArticleViewModel::class.java)

        viewModel.getAllArticles(collectionName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = screenTitle

        viewModel.message3.observe(viewLifecycleOwner, Observer {
            if (_binding == null) return@Observer
            
            if (it.isNullOrEmpty()) {
                binding.recyclerArticleListFrag.adapter = null
                return@Observer
            }

            Log.d("Art All Data", it[0].data.toString())

            Adapter = ArticleListAdapter(requireContext(), it, this)
            binding.recyclerArticleListFrag.adapter = Adapter
            binding.recyclerArticleListFrag.layoutManager = GridLayoutManager(requireContext(), 2)

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArticleListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCellClickListener(name: String) {
        fruitFragment = FruitsFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        fruitFragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fruitFragment, name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("name")
            .commit()
    }
}
