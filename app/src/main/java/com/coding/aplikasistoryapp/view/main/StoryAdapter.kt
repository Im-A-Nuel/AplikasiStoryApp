package com.coding.aplikasistoryapp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coding.aplikasistoryapp.R
import com.coding.aplikasistoryapp.data.remote.response.ListStoryItem
import com.coding.aplikasistoryapp.databinding.ItemViewStoryBinding
import com.coding.aplikasistoryapp.view.detail.DetailStoryActivity

class StoryAdapter(private val onItemClicked: (ListStoryItem) -> Unit) : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DiffCallback) {

    class StoryViewHolder(private val binding: ItemViewStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private var onItemClicked: ((ListStoryItem) -> Unit)? = null

        fun bind(story: ListStoryItem) {
            binding.apply {

                tvTitle.text = story.name
                tvDescription.text = story.description

                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.ic_place_holder) // Optional placeholder image
                    .error(R.drawable.ic_place_holder) // Optional error image
                    .into(imgItemPhoto)

                // Set click listener if needed
                root.setOnClickListener {
                    val id = story.id
                    val intent = Intent(it.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.EXTRA_ID, id)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemViewStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}