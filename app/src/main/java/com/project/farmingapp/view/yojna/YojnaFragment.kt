package com.project.farmingapp.view.yojna

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.FragmentYojnaBinding
import com.project.farmingapp.viewmodel.YojnaViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class YojnaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var yojnaViewModel: YojnaViewModel

    private var _binding: FragmentYojnaBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param1 = it.getString("name") ?: param1
        }
        val schemeId = param1 ?: this.tag.toString()
        Log.d("YojnaFragment", schemeId)

        yojnaViewModel =
            ViewModelProvider(requireActivity()).get(YojnaViewModel::class.java)

        yojnaViewModel.getYojna(schemeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYojnaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YojnaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Krishi Yojna"
        binding.progressYojna.visibility = View.VISIBLE

        yojnaViewModel.msg.observe(viewLifecycleOwner, Observer {
            binding.yojnaTitle.text = it["title"]?.toString() ?: "Scheme"
            binding.yojnaDesc.text = it["description"]?.toString() ?: "-"
            binding.yojnaDate.text = it["launch"]?.toString() ?: "-"
            binding.yojnaLaunchedBy.text = it["headedBy"]?.toString() ?: "-"
            binding.yojnaBudget.text = it["budget"]?.toString() ?: "-"
            val eligibility = it["eligibility"] as? List<String> ?: emptyList()
            val documents = it["documents"] as? List<String> ?: emptyList()
            val objectives = it["objective"] as? List<String> ?: emptyList()

            binding.yojnaEligibility.text = numberedList(eligibility)
            binding.yojnaDocumentsRequired.text = numberedList(documents)
            binding.yojnaObjectives.text = numberedList(objectives)

            binding.yojnaWebsite.text = it["website"]?.toString() ?: "-"
            Glide.with(this).load(it["image"]?.toString()).into(binding.yojnaImage)
            binding.progressYojna.visibility = View.GONE
        })
    }

    private fun numberedList(values: List<String>): String {
        return values.mapIndexed { index, value -> "${index + 1}. $value" }
            .joinToString("\n")
            .ifBlank { "-" }
    }
}
