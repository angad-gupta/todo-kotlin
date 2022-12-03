package com.project.todoapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.todoapp.R
import com.project.todoapp.models.Article
import com.squareup.picasso.Picasso
import java.util.*

class NewsAdapter(private val articleList: ArrayList<Article>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article: Article = articleList[position]
        holder.bindItems(article)
        val imgUrl = article.urlToImage
        if (imgUrl.isNullOrEmpty()) {
            Picasso.get()
                .load( R.drawable.ic_menu_gallery)
                .fit()
                .centerCrop()
                .into(holder.itemView.findViewById<ImageView>(R.id.news_img))
        } else {
            Picasso.get()
                .load(imgUrl)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_menu_gallery)
                .into(holder.itemView.findViewById<ImageView>(R.id.news_img))
        }


    }

    override fun getItemCount(): Int {
        Log.v("article", articleList.size.toString())
        return articleList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_article, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(article: Article) {
            val tvTitle = itemView.findViewById<TextView>(R.id.news_title)
//            val tvDesc = itemView.findViewById<TextView>(R.id.new)
            val tvTimestamp = itemView.findViewById<TextView>(R.id.news_publication_time)


            tvTitle.text = article.title
//            tvDesc.text = article.description
            tvTimestamp.text = article.publishedAt?.replace("T", " ")?.replace("Z", " ")
        }
    }
}