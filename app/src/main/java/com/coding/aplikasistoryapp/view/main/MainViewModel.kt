package com.coding.aplikasistoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserModel
import com.coding.aplikasistoryapp.data.remote.response.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel (private val repository: UserRepository, private val storyRepository: StoryRepository): ViewModel() {

    private val _listStory = MutableLiveData<StoryResponse?>()
    val listStory: MutableLiveData<StoryResponse?> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        getAllStory()
    }

    private fun getAllStory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories()
                _listStory.value = response
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshStories() {
        getAllStory()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logOut() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}