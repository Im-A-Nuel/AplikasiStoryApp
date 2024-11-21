package com.coding.aplikasistoryapp.view.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.coding.aplikasistoryapp.data.StoryRepository
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.pref.UserModel
import com.coding.aplikasistoryapp.data.remote.response.UploadResponse
import java.io.File

class AddStoryViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _uploadResult = MutableLiveData<UploadResponse>()
    private var uploadResult: LiveData<UploadResponse> = _uploadResult

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    fun uploadImage(file: File, description: String): LiveData<UploadResponse> {
        try {
            val result = storyRepository.uploadImage(file, description)
            uploadResult = result
        } catch (e: Exception) {
            _uploadResult.postValue(
                UploadResponse(error = true, message = e.message ?: "An error occurred")
            )
        }

        return uploadResult
    }

    fun setResultData(imageUri: Uri?, description: String) {
        _currentImageUri.value = imageUri
        _description.value = description
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
}
