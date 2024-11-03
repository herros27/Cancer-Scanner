package com.dicoding.asclepius.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.databinding.HistoryViewBinding
import com.dicoding.asclepius.view.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter: ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK){
    // Tambahkan interface untuk listener
    private var onDeleteClickListener: ((HistoryEntity) -> Unit)? = null

    // Fungsi untuk set listener dari activity/fragment
    fun setOnDeleteClickListener(listener: (HistoryEntity) -> Unit) {
        onDeleteClickListener = listener
    }

    class HistoryViewHolder(private val binding: HistoryViewBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(
            history: HistoryEntity,
            onDeleteClick:((HistoryEntity) -> Unit)?
        ){
            with(binding){
                if (history.category == "Cancer"){
                    tvJudul.text = "Kanker"
                }else{
                    tvJudul.text = "Bukan Kanker"
                }
                tvPercentage.text = history.percentage.toString()
                date.text = formatDate(history.time)

                Glide.with(itemView.context)
                    .load(history.imageUri)
                    .into(ivContent)

                btnDelete.setOnClickListener {
                    onDeleteClick?.invoke(history)
                }
            }
        }
        private fun formatDate(inputDate: String): String {
            return try {
                val inputFormat = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault())
                val outputFormat = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault())
                val date: Date? = inputFormat.parse(inputDate)
                if (date != null) {
                    outputFormat.format(date)
                } else {
                    "Invalid date"
                }
            } catch (e: Exception) {
                "Error formatting date: ${e.message}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history, onDeleteClickListener)
    }

    companion object {
        private const val INPUT_FORMAT = "dd-MM-yyyy HH:mm"
        private const val OUTPUT_FORMAT = "dd MMMM yyyy"
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}