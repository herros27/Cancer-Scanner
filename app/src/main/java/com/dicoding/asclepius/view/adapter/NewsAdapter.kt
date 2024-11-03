package com.dicoding.asclepius.view.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.databinding.NewsViewBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NewsAdapter: ListAdapter<ArticlesItem,NewsAdapter.NewsViewHolder>(DIFF_CALLBACK){

    class NewsViewHolder(private val binding: NewsViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(news: ArticlesItem){
            with(binding){
                tvJudul.text = news.title
                tvDesc.text = news.description
                date.text = formatToDateOnly(news.publishedAt.toString())

                Glide.with(itemView.context)
                    .load(news.urlToImage)
                    .into(ivContent)

                binding.root.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(news.url)
                    binding.root.context.startActivity(intent)
                }
            }
        }
        private fun formatToDateOnly(dateString: String, inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", outputPattern: String = "dd MMM yyyy"): String {
            return try {
                val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateString)

                val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString             }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }


    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}



