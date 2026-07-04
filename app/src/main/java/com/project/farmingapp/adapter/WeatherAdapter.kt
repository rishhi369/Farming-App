package com.project.farmingapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.SingleWeatherBinding
import com.project.farmingapp.model.data.WeatherList
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter(val context: Context, val weatherrootdatas: List<WeatherList>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(val binding: SingleWeatherBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = SingleWeatherBinding.inflate(LayoutInflater.from(context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return weatherrootdatas.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weathernew = weatherrootdatas[position]
        val binding = holder.binding

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val parsedDate = try { inputFormat.parse(weathernew.dt_txt.take(10)) } catch (e: Exception) { null }
        val outputDate = parsedDate?.let { outputFormat.format(it) } ?: weathernew.dt_txt.take(10)

        Log.d("New Date", outputDate)

        val we = weathernew.weather.firstOrNull()
        val we2 = weathernew.main
        binding.weatherDate.text = outputDate
        binding.weatherDescription.text = we?.description?.replaceFirstChar { it.uppercase() } ?: "-"
        
        val temp = we2.temp - 273.15
        binding.weatherTemperature.text = temp.toInt().toString() + "\u2103"

        val iconcode = we?.icon.orEmpty()
        if (iconcode.isNotBlank()) {
            val iconurl = "https://openweathermap.org/img/w/$iconcode.png"
            Glide.with(context)
                .load(iconurl)
                .into(binding.weatherIcon)
        }
    }
}
