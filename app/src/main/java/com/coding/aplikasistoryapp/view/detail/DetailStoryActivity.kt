package com.coding.aplikasistoryapp.view.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.coding.aplikasistoryapp.R
import com.coding.aplikasistoryapp.databinding.ActivityDetailStoryBinding
import com.coding.aplikasistoryapp.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val storyId = intent.getStringExtra(EXTRA_ID)
        if (storyId != null) {
            if (storyId.isNotEmpty()) {
                viewModel.getDetailStory(storyId)
            }
        }

        viewModel.detailStory.observe(this) { result ->
            result?.let {
                binding.apply {
                    titleText.text = result.story?.name
                    descriptionText.text = result.story?.description

                    Glide.with(this@DetailStoryActivity)
                        .load(result.story?.photoUrl)
                        .placeholder(R.drawable.ic_place_holder)
                        .error(R.drawable.ic_place_holder)
                        .into(detailImage)
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}