package com.coding.aplikasistoryapp.data

import androidx.lifecycle.liveData
import com.coding.aplikasistoryapp.data.pref.UserPreference
import com.coding.aplikasistoryapp.data.remote.api.ApiService
import com.coding.aplikasistoryapp.data.remote.response.DetailResponse
import com.coding.aplikasistoryapp.data.remote.response.StoryResponse
import com.coding.aplikasistoryapp.data.remote.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStories(): StoryResponse {
        val session = userPreference.getSession().first()
        val token = "Bearer ${session.token}" // Pastikan format token sesuai dengan API
        return apiService.getStories(token)
    }

    suspend fun getStoriesById(id: String): DetailResponse {
        val session = userPreference.getSession().first()
        val token = "Bearer ${session.token}"
        return apiService.getDetailStories(id, token)
    }

    fun uploadImage(imageFile: File, description: String) = liveData<UploadResponse> {
        val session = userPreference.getSession().first()
        val token = "Bearer ${session.token}"
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody, token)
            emit(successResponse)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(errorResponse)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}