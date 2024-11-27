package com.coding.aplikasistoryapp.view

import com.coding.aplikasistoryapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryEntity(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                id = "id$i",
                photoUrl = "https://contoh.com/photo$i.jpg",
                createdAt = "2024-11-26T10:00:00Z",
                name = "User $i",
                description = "Deskripsi cerita $i",
                lon = 107.0 + i,
                lat = -6.0 - i
            )
            storyList.add(story)
        }
        return storyList
    }
}