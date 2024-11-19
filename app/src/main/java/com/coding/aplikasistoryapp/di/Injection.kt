package com.coding.aplikasistoryapp.di

import android.content.Context
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserPreference
import com.coding.aplikasistoryapp.data.pref.dataStore
import com.coding.aplikasistoryapp.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val session = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(session.token)
        return UserRepository.getInstance(apiService, pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val session = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(session.token)
        return StoryRepository.getInstance(apiService, pref)
    }
}