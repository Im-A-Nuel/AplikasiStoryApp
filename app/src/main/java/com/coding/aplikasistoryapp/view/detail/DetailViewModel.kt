package com.coding.aplikasistoryapp.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.remote.response.DetailResponse
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _detailStory = MutableLiveData<DetailResponse?>()
    val detailStory: MutableLiveData<DetailResponse?> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String?>()

    fun getDetailStory(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoriesById(id)
                _detailStory.value = response
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}