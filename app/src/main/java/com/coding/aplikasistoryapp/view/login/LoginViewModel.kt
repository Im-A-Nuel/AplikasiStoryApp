package com.coding.aplikasistoryapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserModel
import com.coding.aplikasistoryapp.data.remote.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                _loginResult.postValue(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val parsedError = Gson().fromJson(errorBody, LoginResponse::class.java)
                val errorResponse = LoginResponse(
                    error = true,
                    message = parsedError?.message
                )
                _loginResult.postValue(errorResponse)
            } catch (e: Exception) {
                _loginResult.postValue(LoginResponse(error = true, message = "Kesalahan jaringan"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}