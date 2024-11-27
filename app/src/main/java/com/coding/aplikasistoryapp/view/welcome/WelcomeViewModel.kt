package com.coding.aplikasistoryapp.view.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WelcomeViewModel : ViewModel() {

    private val _navigateTo = MutableLiveData<NavigateTo?>()
    val navigateTo: LiveData<NavigateTo?> = _navigateTo

    fun onLoginClicked() {
        _navigateTo.value = NavigateTo.LOGIN
    }

    fun onSignupClicked() {
        _navigateTo.value = NavigateTo.SIGNUP
    }

    fun navigationDone() {
        _navigateTo.value = null
    }

    enum class NavigateTo {
        LOGIN, SIGNUP
    }
}
