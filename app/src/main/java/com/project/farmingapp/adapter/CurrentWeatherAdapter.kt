package com.project.farmingapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleCurrentweatherBinding
import com.project.farmingapp.model.data.WeatherList

class CurrentWeatherAdapter(val context: Context, val weatherrootdatas: List<WeatherList>) :
    RecyclerView.Adapter<CurrentWeatherAdapter.CurrentWeatherViewHolder>() {

    class CurrentWeatherViewHolder(val binding: SingleCurrentweatherBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentWeatherViewHolder {
        val binding = SingleCurrentweatherBinding.inflate(LayoutInflater.from(context), parent, false)
        return CurrentWeatherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weatherrootdatas.size
    }

    override fun onBindViewHolder(holder: CurrentWeatherViewHolder, position: Int) {
        val weathernew = weatherrootdatas[position]
        val binding = holder.binding

        binding.temp.text = (weathernew.main.temp - 273.15).toInt().toString() + "\u2103"
        val currentWeather = weathernew.weather.firstOrNull()
        binding.desc.text = currentWeather?.description?.replaceFirstChar { it.uppercase() } ?: "-"

        binding.todayTitle.text = "Today, " + weathernew.dt_txt.drop(10).take(6)

        Log.d("Something", weathernew.dt_txt.drop(10))

        binding.minTemp.text = (weathernew.main.temp_min.toDouble() - 273.1).toInt().toString() + "\u2103"
        binding.currentWeatherContainer.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale)

        binding.maxTemp.text = (weathernew.main.temp_max.toDouble() - 273.1).toInt().toString() + "\u2103"
        binding.humidity.text = weathernew.main.humidity.toString() + "%"
        
        val iconcode = currentWeather?.icon.orEmpty()
        if (iconcode.isNotBlank()) {
            val iconurl = "https://openweathermap.org/img/w/$iconcode.png"
            Glide.with(context)
                .load(iconurl)
                .into(binding.icon)
        }
    }
}
