package com.coding.aplikasistoryapp.view.addstory

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.coding.aplikasistoryapp.R
import com.coding.aplikasistoryapp.databinding.ActivityAddStoryBinding
import com.coding.aplikasistoryapp.util.getImageUri
import com.coding.aplikasistoryapp.util.reduceFileImage
import com.coding.aplikasistoryapp.util.uriToFile
import com.coding.aplikasistoryapp.view.ViewModelFactory

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private var imageUriLast: Uri? = null

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupObservers()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadStory() }
    }

    private fun setupObservers() {
        viewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                binding.previewImageView.setImageURI(uri)
            } else {
                binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
            }
        }
        viewModel.description.observe(this) { description ->
            binding.descriptionText.setText(description)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        imageUriLast = viewModel.currentImageUri.value
        getImageUri(this).let { uri ->
            viewModel.setResultData(uri, binding.descriptionText.text.toString())
            launcherIntentCamera.launch(uri)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.currentImageUri.value?.let { showImage(it) }
        } else {
            viewModel.setResultData(imageUriLast, binding.descriptionText.text.toString())
            imageUriLast?.let { showImage(it) }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setResultData(it, binding.descriptionText.text.toString())
            showImage(it)
            imageUriLast = it
        }
    }

    private fun uploadStory() {
        val description = binding.descriptionText.text.toString()

        viewModel.currentImageUri.value?.let { uri ->
            showLoading(true)
            val imageFile = uriToFile(uri, this).reduceFileImage()

            viewModel.uploadImage(imageFile, description).observe(this) { result ->
                showLoading(false)
                if (result != null) {
                    if (!result.error) {
                        showToast(getString(R.string.upload_success))
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        showToast(getString(R.string.upload_failed))
                    }
                } else {
                    showToast(getString(R.string.error_occurred))
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
