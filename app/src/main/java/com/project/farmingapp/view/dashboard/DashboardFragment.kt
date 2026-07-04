package com.project.farmingapp.view.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.adapter.AgriNewsAdapter
import com.project.farmingapp.adapter.DashboardEcomItemAdapter
import com.project.farmingapp.model.data.WeatherRootList
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.view.articles.ArticleListFragment
import com.project.farmingapp.view.ecommerce.EcommerceItemFragment
import com.project.farmingapp.view.weather.WeatherFragment
import com.project.farmingapp.view.yojna.YojnaListFragment
import com.project.farmingapp.viewmodel.AgriNewsViewModel
import com.project.farmingapp.viewmodel.EcommViewModel
import com.project.farmingapp.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class dashboardFragment : Fragment(), CellClickListener {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var viewModel2: EcommViewModel
    private lateinit var agriNewsViewModel: AgriNewsViewModel
    private var data: WeatherRootList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity())
            .get<WeatherViewModel>(WeatherViewModel::class.java)
        viewModel2 = ViewModelProviders.of(requireActivity())
            .get<EcommViewModel>(EcommViewModel::class.java)
        agriNewsViewModel = ViewModelProviders.of(requireActivity())
            .get<AgriNewsViewModel>(AgriNewsViewModel::class.java)

        if (viewModel.getCoordinates().value.isNullOrEmpty()) {
            viewModel.updateCoordinates(listOf("19.0760", "72.8777", "Mumbai"))
        }

        viewModel2.loadAllEcommItems()
        agriNewsViewModel.loadAgriNews()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Agri India"

        observeDashboardData()
        setupClicks()
    }

    private fun observeDashboardData() {
        viewModel.getCoordinates().observe(viewLifecycleOwner, Observer { coordinates ->
            Log.d("DashFrag", coordinates.toString())
            viewModel.updateNewData()
        })

        viewModel.newDataTrial.observe(viewLifecycleOwner, Observer { weatherData ->
            data = weatherData
            val currentWeather = weatherData?.list?.firstOrNull() ?: return@Observer
            val city = viewModel.getCoordinates().value?.getOrNull(2).orEmpty().ifBlank { "Mumbai" }

            weathTempTextWeathFrag.text = "${(currentWeather.main.temp - 273.15).toInt()}\u2103"
            humidityTextWeathFrag.text = "${currentWeather.main.humidity} %"
            val windKmh = currentWeather.wind.speed * 3.6
            windTextWeathFrag.text = "${String.format("%.1f", windKmh)} km/h"
            weatherCityTitle.text = city

            val iconCode = currentWeather.weather.firstOrNull()?.icon
            if (!iconCode.isNullOrBlank()) {
                val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"
                Glide.with(requireContext()).load(iconUrl).into(weathIconImageWeathFrag)
            }
        })

        viewModel2.ecommLiveData.observe(viewLifecycleOwner, Observer { products ->
            if (products.isNullOrEmpty()) {
                dashboardEcommRecycler.adapter = null
                return@Observer
            }

            val itemsToShow = products.indices.shuffled().take(4)
            dashboardEcommRecycler.adapter =
                DashboardEcomItemAdapter(requireContext(), products, itemsToShow, this)
            dashboardEcommRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })

        agriNewsViewModel.newsStatus.observe(viewLifecycleOwner, Observer { status ->
            agriNewsStatusText.text = status
            agriNewsStatusText.visibility = if (status.isNullOrBlank()) View.GONE else View.VISIBLE
        })

        agriNewsViewModel.newsItems.observe(viewLifecycleOwner, Observer { news ->
            if (news.isNullOrEmpty()) {
                agriNewsRecycler.adapter = null
                agriNewsRecycler.visibility = View.GONE
                return@Observer
            }

            agriNewsRecycler.visibility = View.VISIBLE
            agriNewsRecycler.adapter = AgriNewsAdapter(requireContext(), news)
            agriNewsRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })
    }

    private fun setupClicks() {
        weatherCard.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, WeatherFragment(), "weatherFrag")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("weatherFrag")
                .commit()
            data?.let { viewModel.messageToB(it) }
        }

        cat1.setOnClickListener { openArticleList("article_plants", "Plant Articles") }
        cat2.setOnClickListener { openArticleList("article_methods", "Farming Methods") }
        cat3.setOnClickListener { openArticleList("article_diseases", "Crop Diseases") }
        cat4.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, YojnaListFragment(), "yojnaListFrag")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("yojnaListFrag")
                .commit()
        }
        cat5.setOnClickListener { openArticleList("article_fruits", "Fruit Articles") }
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

    private fun openArticleList(collectionName: String, title: String) {
        val articleListFragment = ArticleListFragment()
        articleListFragment.arguments = Bundle().apply {
            putString("collectionName", collectionName)
            putString("title", title)
        }

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, articleListFragment, "articlesListFrag")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("articlesListFrag")
            .commit()
    }
}
