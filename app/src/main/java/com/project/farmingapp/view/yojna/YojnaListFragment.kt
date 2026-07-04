package com.project.farmingapp.view.yojna

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.R
import com.project.farmingapp.adapter.YojnaAdapter
import com.project.farmingapp.databinding.FragmentYojnaListBinding
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.YojnaViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class YojnaListFragment : Fragment(), CellClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: YojnaViewModel

    private var _binding: FragmentYojnaListBinding? = null
    private val binding get() = _binding!!

    lateinit var Adapter: YojnaAdapter
    lateinit var yojnaFragment: YojnaFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
       viewModel = ViewModelProvider(requireActivity())
           .get(YojnaViewModel::class.java)

       viewModel.getAllYojna("yojnas")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYojnaListBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.message3.observe(viewLifecycleOwner, Observer {

            if (it.isNullOrEmpty()) {
                binding.rcyclrYojnaList.adapter = null
                return@Observer
            }

            Log.d("Art All Data", it.first().data.toString())


            Adapter = YojnaAdapter(requireContext(), it, this)
            binding.rcyclrYojnaList.adapter = Adapter
            binding.rcyclrYojnaList.layoutManager = LinearLayoutManager(requireContext())

        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Krishi Yojna"
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YojnaListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCellClickListener(name: String) {
        yojnaFragment = YojnaFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        yojnaFragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, yojnaFragment, name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("yojnaListFrag")
            .commit()
    }
}
