package com.coding.aplikasistoryapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserModel
import com.coding.aplikasistoryapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    val listStory: LiveData<PagingData<ListStoryItem>> by lazy {
        val storyLiveData = storyRepository.getStory()
        Log.d("MainViewModel", "storyRepository.getStory() returned valid data")
        storyLiveData.cachedIn(viewModelScope)
    }

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logOut() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
