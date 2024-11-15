package com.coding.aplikasistoryapp.di

import android.content.Context
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserPreference
import com.coding.aplikasistoryapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref =  UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}