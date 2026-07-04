package com.project.farmingapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.model.data.WeatherList
import kotlinx.android.synthetic.main.post_with_image_sm.view.*
import kotlinx.android.synthetic.main.single_currentweather.view.*

class CurrentWeatherAdapter(val context: Context, val weatherrootdatas:List<WeatherList>):
    RecyclerView.Adapter<CurrentWeatherAdapter.CurrentWeatherViewHolder>() {
    class CurrentWeatherViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var Ctemp=itemView.findViewById<TextView>(R.id.temp)
        var Cwedesc=itemView.findViewById<TextView>(R.id.desc)
        var Cwelogo=itemView.findViewById<ImageView>(R.id.icon)
        var CminTemp=itemView.findViewById<TextView>(R.id.minTemp)
        var CmaxTemp=itemView.findViewById<TextView>(R.id.maxTemp)
        var Chumidity=itemView.findViewById<TextView>(R.id.humidity)
        var CtodayTitle=itemView.findViewById<TextView>(R.id.todayTitle)
        var continer = itemView.findViewById<ConstraintLayout>(R.id.currentWeatherContainer)





    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentWeatherAdapter.CurrentWeatherViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.single_currentweather,parent,false)
        return CurrentWeatherAdapter.CurrentWeatherViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherrootdatas.size
    }

    override fun onBindViewHolder(holder: CurrentWeatherAdapter.CurrentWeatherViewHolder, position: Int) {
        val weathernew =weatherrootdatas[position]
        holder.Ctemp.text = (weathernew.main.temp - 273.15).toInt().toString() + "\u2103"
        val currentWeather = weathernew.weather.firstOrNull()
        holder.Cwedesc.text = currentWeather?.description?.replaceFirstChar { it.uppercase() } ?: "-"

        holder.CtodayTitle.text = "Today, " + weathernew.dt_txt.drop(10).take(6)


        Log.d("Something", weathernew.dt_txt.drop(10))

//        var tempMin = ""
//        for(a in weathernew.main.temp_min){
//        }
        holder.CminTemp.text = (weathernew.main.temp_min.toDouble() - 273.1).toInt().toString()+ "\u2103"
//        holder.continer.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale)

//        holder.itemView.setOnFocusChangeListener { view, b ->
//
//        }
        holder.itemView.currentWeatherContainer.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale)

        

        holder.CmaxTemp.text = (weathernew.main.temp_max.toDouble() - 273.1).toInt().toString() + "\u2103"
        holder.Chumidity.text = weathernew.main.humidity.toString() + "%"
        var iconcode = currentWeather?.icon.orEmpty()
        var iconurl = "https://openweathermap.org/img/w/" + iconcode + ".png";

        Glide.with(holder.itemView.context)
            .load(iconurl)
            .into(holder.Cwelogo)
    }
}
