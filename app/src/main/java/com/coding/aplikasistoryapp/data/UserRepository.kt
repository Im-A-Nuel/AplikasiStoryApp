package com.coding.aplikasistoryapp.data

import com.coding.aplikasistoryapp.data.pref.UserModel
import com.coding.aplikasistoryapp.data.pref.UserPreference
import com.coding.aplikasistoryapp.data.remote.api.ApiService
import com.coding.aplikasistoryapp.data.remote.response.LoginResponse
import com.coding.aplikasistoryapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
    ) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)

        val loginResult = response.loginResult ?: throw IllegalStateException("Login result is null")
        val userModel = UserModel(
            userId = loginResult.userId ?: "",
            name = loginResult.name ?: "",
            token = loginResult.token ?: "",
            isLogin = true
        )
        saveSession(userModel)

        return response
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}