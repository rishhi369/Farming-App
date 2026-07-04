package com.project.farmingapp.view.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.adapter.CurrentWeatherAdapter
import com.project.farmingapp.adapter.WeatherAdapter
import com.project.farmingapp.databinding.FragmentWeatherBinding
import com.project.farmingapp.model.data.WeatherList
import com.project.farmingapp.view.dashboard.DashboardActivity
import com.project.farmingapp.viewmodel.WeatherListener
import com.project.farmingapp.viewmodel.WeatherViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WeatherFragment : Fragment(), WeatherListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: WeatherViewModel

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProvider(requireActivity())
            .get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Weather Forecast"

        if (viewModel.getCoordinates().value.isNullOrEmpty()) {
            viewModel.updateCoordinates(listOf("19.0760", "72.8777", "Mumbai"))
        }

        binding.currentWeatherRcycl.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcylrWeather.layoutManager = LinearLayoutManager(requireContext())

        binding.useMyLocationWeatherBtn.setOnClickListener {
            (activity as? DashboardActivity)?.automatedClick()
        }

        viewModel.getCoordinates().observe(viewLifecycleOwner, Observer { coordinates ->
            binding.weatherCity.text = coordinates.getOrNull(2).orEmpty().ifBlank { "Mumbai" }
            viewModel.updateNewData()
        })

        viewModel.newDataTrial.observe(viewLifecycleOwner, Observer { weatherData ->
            val forecasts = weatherData?.list.orEmpty()
            renderForecasts(forecasts)
        })

        viewModel.updateNewData()
        renderForecasts(viewModel.newDataTrial.value?.list.orEmpty())
    }

    private fun renderForecasts(forecasts: List<WeatherList>) {
        if (forecasts.isEmpty()) {
            binding.currentWeatherRcycl.adapter = CurrentWeatherAdapter(requireContext(), emptyList())
            binding.rcylrWeather.adapter = WeatherAdapter(requireContext(), emptyList())
            return
        }

        val firstDay = forecasts.firstOrNull()?.dt_txt?.take(10)
        val todayItems = forecasts.filter { it.dt_txt.take(10) == firstDay }.take(8)
        val dailyItems = forecasts
            .filter { it.dt_txt.length >= 13 && it.dt_txt.substring(11, 13) == "12" }
            .take(5)

        binding.currentWeatherRcycl.adapter =
            CurrentWeatherAdapter(requireContext(), todayItems)
        binding.rcylrWeather.adapter =
            WeatherAdapter(requireContext(), dailyItems.ifEmpty { forecasts.take(5) })
    }

    override fun onSuccess(authRepo: LiveData<String>) {
        authRepo.observe(viewLifecycleOwner, Observer {
            Log.d("Frag", authRepo.value.toString())
        })
    }
}
