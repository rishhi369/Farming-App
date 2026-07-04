package com.project.farmingapp.view.articles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.FragmentFruitsBinding
import com.project.farmingapp.viewmodel.ArticleListener
import com.project.farmingapp.viewmodel.ArticleViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FruitsFragment : Fragment(), ArticleListener {
    private var param1: String? = null
    private var param2: String? = null
    private var articleTitle: String? = null
    private lateinit var viewModel: ArticleViewModel

    private var _binding: FragmentFruitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            articleTitle = it.getString("name")
        }

        viewModel = ViewModelProvider(requireActivity())
            .get(ArticleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFruitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Articles"

        setupDescriptionToggle()
        renderSelectedArticle()
    }

    private fun setupDescriptionToggle() {
        var toggle = 0
        binding.descToggleBtnFruitFragArt.setOnClickListener {
            if (toggle == 0) {
                binding.descTextValueFruitFragArt.maxLines = Integer.MAX_VALUE
                toggle = 1
                rotateDescriptionButton(0.0f, 180f)
            } else {
                binding.descTextValueFruitFragArt.maxLines = 3
                toggle = 0
                rotateDescriptionButton(180f, 0f)
            }
        }
    }

    private fun rotateDescriptionButton(from: Float, to: Float) {
        val rotateAnim = RotateAnimation(
            from,
            to,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnim.duration = 2
        rotateAnim.fillAfter = true
        binding.descToggleBtnFruitFragArt.startAnimation(rotateAnim)
    }

    private fun renderSelectedArticle() {
        val selectedTitle = articleTitle ?: tag
        val article = viewModel.message3.value
            ?.firstOrNull { it.getString("title") == selectedTitle }
            ?.data

        if (article == null) {
            binding.titleTextFruitFragArt.text = selectedTitle ?: "Article"
            binding.descTextValueFruitFragArt.text = "Article details are not available yet."
            binding.progressArticle.visibility = View.GONE
            return
        }

        val attributes = article["attributes"] as? Map<String, Any> ?: emptyMap()
        val diseases = article["diseases"] as? List<String> ?: emptyList()
        val images = article["images"] as? List<String> ?: emptyList()

        binding.tempTextFruitFragArt.text = attributes["Temperature"]?.toString() ?: "-"
        binding.monthTextFruitFragArt.text = attributes["Time"]?.toString() ?: "-"
        binding.titleTextFruitFragArt.text = article["title"]?.toString() ?: "Article"
        binding.descTextValueFruitFragArt.text = article["description"]?.toString() ?: "-"
        binding.processTextValueFruitFragArt.text = article["process"]?.toString() ?: "-"
        binding.soilTextValueFruitFragArt.text = article["soil"]?.toString() ?: "-"
        binding.stateTextValueFruitFragArt.text = article["state"]?.toString() ?: "-"
        binding.attr1ValueFruitFragArt.text = attributes["Weight"]?.toString() ?: "-"
        binding.attr2ValueFruitFragArt.text = attributes["Vitamins"]?.toString() ?: "-"
        binding.attr3ValueFruitFragArt.text = attributes["Tree Height"]?.toString() ?: "-"
        binding.attr4ValueFruitFragArt.text = attributes["growthTime"]?.toString() ?: "-"

        Glide.with(this).load(images.firstOrNull()).into(binding.imageFruitFragArt)
        binding.diseaseTextValueFruitFragArt.text = diseases.mapIndexed { index, disease ->
            "${index + 1}. $disease"
        }.joinToString("\n")

        binding.progressArticle.visibility = View.GONE
    }

    override fun onStarted() {
        Log.d("Fruit", "Started")
    }

    override fun onSuccess(authRepo: LiveData<String>) {
        authRepo.observe(viewLifecycleOwner, Observer {
            Log.d("Fruit", "Success")
        })
    }

    override fun onFailure(message: String) {
        Log.d("Fruit", message)
    }
}
