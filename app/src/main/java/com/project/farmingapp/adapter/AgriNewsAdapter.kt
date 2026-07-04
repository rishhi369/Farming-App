package com.project.farmingapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleAgriNewsItemBinding
import com.project.farmingapp.model.data.AgriNewsItem

class AgriNewsAdapter(
    private val context: Context,
    private val allData: List<AgriNewsItem>
) : RecyclerView.Adapter<AgriNewsAdapter.AgriNewsViewHolder>() {

    class AgriNewsViewHolder(val binding: SingleAgriNewsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgriNewsViewHolder {
        val binding = SingleAgriNewsItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return AgriNewsViewHolder(binding)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: AgriNewsViewHolder, position: Int) {
        val currentData = allData[position]
        val binding = holder.binding
        
        binding.agriNewsTitle.text = currentData.title
        binding.agriNewsSource.text = currentData.source
        binding.agriNewsDate.text = currentData.publishedAt

        binding.root.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentData.link))
            context.startActivity(browserIntent)
        }
    }
}
