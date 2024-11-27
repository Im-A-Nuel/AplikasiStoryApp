package com.coding.aplikasistoryapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.remote.response.StoryResponse
import kotlinx.coroutines.launch

class MapsViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _listStory = MutableLiveData<StoryResponse?>()
    val listStory: LiveData<StoryResponse?> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchStoriesWithLocation()
    }

    private fun fetchStoriesWithLocation() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoriesWithLocation()
                _listStory.value = response
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
