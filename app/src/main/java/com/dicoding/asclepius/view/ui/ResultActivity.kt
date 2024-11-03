package com.dicoding.asclepius.view.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.viewModel.MainViewModel
import com.dicoding.asclepius.view.viewModel.ViewModelFactory
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var mainViewModel: MainViewModel

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]

        setupToolbar()
        showImage()
        showResult()
    }

    private fun setupToolbar(){
        setSupportActionBar(binding.actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.actionBar.navigationIcon?.setTint(Color.WHITE)
    }

    private fun showImage() {
                val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMG_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        } ?: run{
            "Image is missing!!".showError()
        }
    }

    private fun String.showError() {
        Toast.makeText(this@ResultActivity, this, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun showResult(){
        val category = intent.getStringExtra(EXTRA_RESULT)
        val score = intent.getFloatExtra(EXTRA_SCORE, 0f)
        val percentage = score * 100
        val intPercent = percentage.toInt()
        val imgUri = intent.getStringExtra(EXTRA_IMG_URI)

        if (category != null) {
            if (category == "Cancer"){
                binding.resultText.text = "Hasilnya adalah Kanker dengan akurasi $intPercent %"
            }else{
                binding.resultText.text = "Hasilnya adalah bukan Kanker dengan akurasi $intPercent %"
            }

        }else{
            getString(R.string.result_not_available).also { binding.resultText.text = it }
        }

        binding.saveButton.setOnClickListener {
            saveHistory(imgUri,category,intPercent)
        }

    }

    private fun saveHistory(imageUri:String?, result: String?, percentage:Int){
        try {
            if(imageUri != null && result != null){
                val imageByteArray = convertUriToByteArray(Uri.parse(imageUri))
                val time = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

                val history = HistoryEntity(
                    category = result,
                    percentage = percentage,
                    imageUri = imageByteArray,
                    time = time
                )

                mainViewModel.saveHistory(history).observe(this){ saves ->
                    when(saves){
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            Toast.makeText(this, "History saved successfully", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            }
                        is Result.Error -> {
                            Toast.makeText(this, "Failed to saving history", Toast.LENGTH_SHORT)
                                .show()
                            binding.progressBar.visibility =View.VISIBLE
                        }

                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this, "Error Saving History:  ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertUriToByteArray(uri: Uri): ByteArray{
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputSream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputSream)

        return outputSream.toByteArray()
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

    companion object{
        const val EXTRA_IMG_URI = "extra_img_uri"
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_RESULT = "extra_result"
    }


}