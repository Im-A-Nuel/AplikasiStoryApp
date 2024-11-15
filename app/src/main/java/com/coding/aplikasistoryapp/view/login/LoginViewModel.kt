package com.coding.aplikasistoryapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository): ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}