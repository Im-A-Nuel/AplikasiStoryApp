package com.coding.aplikasistoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel (private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logOut() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}