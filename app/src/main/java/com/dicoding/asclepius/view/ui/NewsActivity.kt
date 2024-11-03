package com.dicoding.asclepius.view.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.view.adapter.NewsAdapter
import com.dicoding.asclepius.view.viewModel.MainViewModel
import com.dicoding.asclepius.view.viewModel.ViewModelFactory

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var adapter : NewsAdapter
    private lateinit var rvNews: RecyclerView
    private val viewModel : MainViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRv()
    }
    
    private fun setupRv(){
        rvNews = binding.rvNews
        rvNews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        
        adapter = NewsAdapter()
        
        rvNews.adapter = adapter
        
        viewModel.newsArticles.observe(this) { news ->
            when(news){
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this,"Failed to loaded data: ${news.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    if (news.data.isEmpty()){
                        binding.progressBar.visibility = View.VISIBLE
                    }else{
                        adapter.submitList(news.data)
                        Log.d("NewsActivity", news.data[0].publishedAt ?: "data kosong")
                        binding.progressBar.visibility = View.GONE
                    }
                    
                }
            }
            
        }
    }

    private fun setupToolbar(){
        setSupportActionBar(binding.actionBar)
//        supportActionBar?.title = "News"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.actionBar.navigationIcon?.setTint(Color.WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}