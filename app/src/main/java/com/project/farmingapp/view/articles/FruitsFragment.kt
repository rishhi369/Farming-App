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
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.utilities.hide
import com.project.farmingapp.viewmodel.ArticleListener
import com.project.farmingapp.viewmodel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_fruits.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FruitsFragment : Fragment(), ArticleListener {
    private var param1: String? = null
    private var param2: String? = null
    private var articleTitle: String? = null
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            articleTitle = it.getString("name")
        }

        viewModel = ViewModelProviders.of(requireActivity())
            .get<ArticleViewModel>(ArticleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fruits, container, false)
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
        descToggleBtnFruitFragArt.setOnClickListener {
            if (toggle == 0) {
                descTextValueFruitFragArt.maxLines = Integer.MAX_VALUE
                toggle = 1
                rotateDescriptionButton(0.0f, 180f)
            } else {
                descTextValueFruitFragArt.maxLines = 3
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
        descToggleBtnFruitFragArt.startAnimation(rotateAnim)
    }

    private fun renderSelectedArticle() {
        val selectedTitle = articleTitle ?: tag
        val article = viewModel.message3.value
            ?.firstOrNull { it.getString("title") == selectedTitle }
            ?.data

        if (article == null) {
            titleTextFruitFragArt.text = selectedTitle ?: "Article"
            descTextValueFruitFragArt.text = "Article details are not available yet."
            progressArticle.hide()
            return
        }

        val attributes = article["attributes"] as? Map<String, Any> ?: emptyMap()
        val diseases = article["diseases"] as? List<String> ?: emptyList()
        val images = article["images"] as? List<String> ?: emptyList()

        tempTextFruitFragArt.text = attributes["Temperature"]?.toString() ?: "-"
        monthTextFruitFragArt.text = attributes["Time"]?.toString() ?: "-"
        titleTextFruitFragArt.text = article["title"]?.toString() ?: "Article"
        descTextValueFruitFragArt.text = article["description"]?.toString() ?: "-"
        processTextValueFruitFragArt.text = article["process"]?.toString() ?: "-"
        soilTextValueFruitFragArt.text = article["soil"]?.toString() ?: "-"
        stateTextValueFruitFragArt.text = article["state"]?.toString() ?: "-"
        attr1ValueFruitFragArt.text = attributes["Weight"]?.toString() ?: "-"
        attr2ValueFruitFragArt.text = attributes["Vitamins"]?.toString() ?: "-"
        attr3ValueFruitFragArt.text = attributes["Tree Height"]?.toString() ?: "-"
        attr4ValueFruitFragArt.text = attributes["growthTime"]?.toString() ?: "-"

        Glide.with(this).load(images.firstOrNull()).into(imageFruitFragArt)
        diseaseTextValueFruitFragArt.text = diseases.mapIndexed { index, disease ->
            "${index + 1}. $disease"
        }.joinToString("\n")

        progressArticle.hide()
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
