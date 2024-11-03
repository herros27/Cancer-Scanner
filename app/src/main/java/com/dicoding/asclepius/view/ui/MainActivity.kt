package com.dicoding.asclepius.view.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()

        imageClassifierHelper = ImageClassifierHelper(this, this)

        savedInstanceState?.getString(CURRENT_IMG_URI)?.let {
            currentImageUri = Uri.parse(it)
            showImage()
        }

        binding.galleryButton.setOnClickListener { startGallery() }

        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    private fun setupBottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_news -> {
                    startActivity(Intent(this, NewsActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    @Deprecated("This method has been deprecated...")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val croppedUri = UCrop.getOutput(data!!)
            if (croppedUri != null) {
                currentImageUri = croppedUri
                showImage()
            } else {
                showToast("Crop failed, no output URI")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            handleImg(it)
        } ?: showToast("No media selected")
    }

    private fun starCropImg(uri: Uri) {
        val option = getUCropOptions()
        val uCrop = UCrop.of(uri, getImgUriCropped())
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(option)

        uCrop.start(this)
    }

    private fun handleImg(uri: Uri) {
        starCropImg(uri)
        currentImageUri = uri
        showImage()
    }

    private fun getUCropOptions() = UCrop.Options().apply {
        setCompressionQuality(90)
        setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.primary_red_light))
        setActiveControlsWidgetColor(
            ContextCompat.getColor(this@MainActivity, R.color.surface_dark)
        )
        setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.primary_red_light))
        setToolbarWidgetColor(Color.WHITE)
    }

    private fun showImage() {
        if (currentImageUri != null) {
            Log.d("Image URI", "showImage: $currentImageUri")
            binding.previewImageView.setImageURI(currentImageUri)
            binding.emptyTextView.visibility = View.GONE
        }
    }


    private fun getImgUriCropped() = Uri.fromFile(File(cacheDir, "cropped_img"))

    private fun analyzeImage() {
        binding.progressIndicator.visibility = View.VISIBLE
        currentImageUri?.let {
            SHOULD_IMG_RESET = true // Set the flag to reset the image
            imageClassifierHelper.classifyStaticImage(it)
        } ?: Toast.makeText(this, getString(R.string.empty_img), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        if (SHOULD_IMG_RESET) {
            // Reset the image and URI when returning from ResultActivity
            currentImageUri = null
            binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
            binding.emptyTextView.visibility = View.VISIBLE
            SHOULD_IMG_RESET = false // Reset the flag
        }
    }

    private fun moveToResult(result: String, confidence: Float) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_RESULT, result)
        intent.putExtra(ResultActivity.EXTRA_SCORE, confidence)
        intent.putExtra(ResultActivity.EXTRA_IMG_URI, currentImageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        binding.progressIndicator.visibility = View.GONE
        showToast(error)
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        runOnUiThread {
            try {
                results?.let {
                    val category = it[0].categories[0].label
                    val confidence = it[0].categories[0].score
                    moveToResult(category, confidence)
                }
            } catch (e: Exception) {
                onError(e.message.toString())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let {
            outState.putString(CURRENT_IMG_URI, it.toString())

        }
    }

    companion object{
        private const val CURRENT_IMG_URI = "currentImageUri"
        // Add a flag to control image resetting
        private var SHOULD_IMG_RESET = false
    }
}
