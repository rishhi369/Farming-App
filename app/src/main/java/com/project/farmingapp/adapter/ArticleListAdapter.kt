package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.databinding.ArticleListSingleBinding
import com.project.farmingapp.utilities.CellClickListener

class ArticleListAdapter(
    val context: Context,
    val articleListData: List<DocumentSnapshot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<ArticleListAdapter.ArticleListViewholder>() {

    class ArticleListViewholder(val binding: ArticleListSingleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListViewholder {
        val binding = ArticleListSingleBinding.inflate(LayoutInflater.from(context), parent, false)
        return ArticleListViewholder(binding)
    }

    override fun getItemCount(): Int {
        return articleListData.size
    }

    override fun onBindViewHolder(holder: ArticleListViewholder, position: Int) {
        val singleArticle = articleListData[position]
        val binding = holder.binding

        binding.descTextxArticleListFrag.text = singleArticle.getString("title") ?: "Article"
        binding.articleListCardArtListFrag.setOnClickListener {
            cellClickListener.onCellClickListener(singleArticle.getString("title") ?: singleArticle.id)
        }
        
        val list = singleArticle.get("images") as? List<String> ?: emptyList()
        Glide.with(context)
            .load(list.firstOrNull())
            .into(binding.imageArticleListFrag)
    }
}
