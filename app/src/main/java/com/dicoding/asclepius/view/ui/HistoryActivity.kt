package com.dicoding.asclepius.view.ui

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.view.adapter.HistoryAdapter
import com.dicoding.asclepius.view.viewModel.MainViewModel
import com.dicoding.asclepius.view.viewModel.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter : HistoryAdapter
    private lateinit var rvHistory: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]

        setupToolbar()
        setupRv()
    }

    private fun setupToolbar(){
        setSupportActionBar(binding.actionBar)
//        supportActionBar?.title = "History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.actionBar.navigationIcon?.setTint(Color.WHITE)
    }

    private fun setupRv(){
        rvHistory = binding.rvHistory

        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2

        rvHistory.layoutManager = GridLayoutManager(this,spanCount)

        adapter = HistoryAdapter()

        rvHistory.adapter = adapter

        adapter.setOnDeleteClickListener { history ->
            showDeleteConfirmationDialog(history.id )
        }

        mainViewModel.history.observe(this) { history ->
            when (history) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to loaded data...", Toast.LENGTH_SHORT).show()
                }

                is Result.Success -> {

                    if (history.data.isEmpty()) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        adapter.submitList(history.data)
                        Log.d("HistoryActivity", history.data[0].time)
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Succes to loaded data...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus data ini?")
            .setPositiveButton("Ya") { _, _ ->
                mainViewModel.deleteHistory(id)
                Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
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