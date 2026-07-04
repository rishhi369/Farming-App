package com.project.farmingapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.R
import com.project.farmingapp.model.data.AgriNewsItem
import kotlinx.android.synthetic.main.single_agri_news_item.view.*

class AgriNewsAdapter(
    private val context: Context,
    private val allData: List<AgriNewsItem>
) : RecyclerView.Adapter<AgriNewsAdapter.AgriNewsViewHolder>() {

    class AgriNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgriNewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_agri_news_item, parent, false)
        return AgriNewsViewHolder(view)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: AgriNewsViewHolder, position: Int) {
        val currentData = allData[position]
        holder.itemView.agriNewsTitle.text = currentData.title
        holder.itemView.agriNewsSource.text = currentData.source
        holder.itemView.agriNewsDate.text = currentData.publishedAt

        holder.itemView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentData.link))
            context.startActivity(browserIntent)
        }
    }
}
