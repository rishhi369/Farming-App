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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.adapter.AgriNewsAdapter
import com.project.farmingapp.adapter.DashboardEcomItemAdapter
import com.project.farmingapp.databinding.FragmentDashboardBinding
import com.project.farmingapp.model.data.WeatherRootList
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.view.articles.ArticleListFragment
import com.project.farmingapp.view.ecommerce.EcommerceItemFragment
import com.project.farmingapp.view.weather.WeatherFragment
import com.project.farmingapp.view.yojna.YojnaListFragment
import com.project.farmingapp.viewmodel.AgriNewsViewModel
import com.project.farmingapp.viewmodel.EcommViewModel
import com.project.farmingapp.viewmodel.WeatherViewModel

class dashboardFragment : Fragment(), CellClickListener {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var viewModel2: EcommViewModel
    private lateinit var agriNewsViewModel: AgriNewsViewModel
    private var data: WeatherRootList? = null

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(WeatherViewModel::class.java)
        viewModel2 = ViewModelProvider(requireActivity())
            .get(EcommViewModel::class.java)
        agriNewsViewModel = ViewModelProvider(requireActivity())
            .get(AgriNewsViewModel::class.java)

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
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            if (_binding == null) return@Observer
            data = weatherData
            val currentWeather = weatherData?.list?.firstOrNull() ?: return@Observer
            val city = viewModel.getCoordinates().value?.getOrNull(2).orEmpty().ifBlank { "Mumbai" }

            binding.weathTempTextWeathFrag.text = "${(currentWeather.main.temp - 273.15).toInt()}\u2103"
            binding.humidityTextWeathFrag.text = "${currentWeather.main.humidity} %"
            val windKmh = currentWeather.wind.speed * 3.6
            binding.windTextWeathFrag.text = "${String.format("%.1f", windKmh)} km/h"
            binding.weatherCityTitle.text = city

            val iconCode = currentWeather.weather.firstOrNull()?.icon
            if (!iconCode.isNullOrBlank()) {
                val iconUrl = "https://openweathermap.org/img/w/$iconCode.png"
                Glide.with(requireContext()).load(iconUrl).into(binding.weathIconImageWeathFrag)
            }
        })

        viewModel2.ecommLiveData.observe(viewLifecycleOwner, Observer { products ->
            if (_binding == null) return@Observer
            if (products.isNullOrEmpty()) {
                binding.dashboardEcommRecycler.adapter = null
                return@Observer
            }

            val itemsToShow = products.indices.shuffled().take(4)
            binding.dashboardEcommRecycler.adapter =
                DashboardEcomItemAdapter(requireContext(), products, itemsToShow, this)
            binding.dashboardEcommRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })

        agriNewsViewModel.newsStatus.observe(viewLifecycleOwner, Observer { status ->
            if (_binding == null) return@Observer
            binding.agriNewsStatusText.text = status
            binding.agriNewsStatusText.visibility = if (status.isNullOrBlank()) View.GONE else View.VISIBLE
        })

        agriNewsViewModel.newsItems.observe(viewLifecycleOwner, Observer { news ->
            if (_binding == null) return@Observer
            if (news.isNullOrEmpty()) {
                binding.agriNewsRecycler.adapter = null
                binding.agriNewsRecycler.visibility = View.GONE
                return@Observer
            }

            binding.agriNewsRecycler.visibility = View.VISIBLE
            binding.agriNewsRecycler.adapter = AgriNewsAdapter(requireContext(), news)
            binding.agriNewsRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        })
    }

    private fun setupClicks() {
        binding.weatherCard.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, WeatherFragment(), "weatherFrag")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("weatherFrag")
                .commit()
            data?.let { viewModel.messageToB(it) }
        }

        binding.cat1.setOnClickListener { openArticleList("article_plants", "Plant Articles") }
        binding.cat2.setOnClickListener { openArticleList("article_methods", "Farming Methods") }
        binding.cat3.setOnClickListener { openArticleList("article_diseases", "Crop Diseases") }
        binding.cat4.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, YojnaListFragment(), "yojnaListFrag")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("yojnaListFrag")
                .commit()
        }
        binding.cat5.setOnClickListener { openArticleList("article_fruits", "Fruit Articles") }
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
