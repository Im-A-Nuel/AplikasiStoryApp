package com.coding.aplikasistoryapp.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.coding.aplikasistoryapp.R
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.runBlocking

class StackRemoteViewsFactory(
    private val context: Context,
    private val storyRepository: StoryRepository
) : RemoteViewsService.RemoteViewsFactory {

    private val stories = mutableListOf<ListStoryItem>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        runBlocking {
            try {
                val response = storyRepository.getStories()
                val fetchedStories = response.listStory
                stories.clear()

                fetchedStories.forEach { story ->
                    if (story.photoUrl != null) {
                        val localUri = Uri.parse(story.photoUrl)
                        story.photoUrl = localUri.toString()
                    }
                }
                stories.addAll(fetchedStories)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val story = stories[position]
        val rv = RemoteViews(context.packageName, R.layout.widget_item)

        rv.setTextViewText(R.id.textView, story.name ?: "No Name")

        if (!story.photoUrl.isNullOrEmpty()) {
            try {
                val bitmap = runBlocking {
                    Glide.with(context)
                        .asBitmap()
                        .load(story.photoUrl)
                        .submit()
                        .get()
                }
                rv.setImageViewBitmap(R.id.imageView, bitmap)
            } catch (e: Exception) {
                Log.e("StackRemoteViewsFactory", "Error loading image", e)
            }
        }

        val fillInIntent = Intent().apply {
            putExtra(StoryAppWidget.EXTRA_ITEM, position)
        }
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }


    override fun onDestroy() {
        stories.clear()
    }

    override fun getCount(): Int = stories.size

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
