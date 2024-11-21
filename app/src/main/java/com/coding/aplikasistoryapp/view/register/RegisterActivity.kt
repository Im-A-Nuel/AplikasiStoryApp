package com.coding.aplikasistoryapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.coding.aplikasistoryapp.data.UserRepository
import com.coding.aplikasistoryapp.data.remote.response.ErrorResponse
import com.coding.aplikasistoryapp.databinding.ActivityRegisterBinding
import com.coding.aplikasistoryapp.di.Injection
import com.coding.aplikasistoryapp.util.CustomEmailEditText
import com.coding.aplikasistoryapp.util.CustomPasswordEditText
import com.coding.aplikasistoryapp.view.welcome.WelcomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val repository: UserRepository = Injection.provideUserRepository(this)

    private lateinit var name: TextInputEditText
    private lateinit var email: CustomEmailEditText
    private lateinit var password: CustomPasswordEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        name = binding.nameEditText
        email = binding.emailEditText
        password = binding.passwordEditText

        email.addTextChangedListener { checkFormValidity(email, password) }
        password.addTextChangedListener { checkFormValidity(email, password) }

        binding.signupButton.setOnClickListener {
            register()
        }

        binding.signupButton.isEnabled = false
    }

    private fun checkFormValidity(
        email: CustomEmailEditText,
        password: CustomPasswordEditText,
    ) {
        val isEmailValid = email.error == null && email.text?.isNotEmpty() == true
        val isPasswordValid = password.error == null && password.text?.isNotEmpty() == true
        binding.signupButton.isEnabled = isEmailValid && isPasswordValid
    }

    private fun register() {
        val nameText = name.text.toString()
        val emailText = email.text.toString()
        val passText = password.text.toString()

        showLoading(true)

        println("name $nameText")
        println("email $emailText")
        println("pass $passText")

        lifecycleScope.launch {
            try {
                val message = repository.register(
                    nameText,
                    emailText,
                    passText
                )

                if (message.error != true) {
                    showLoading(false)
                    showSuccess(message.message.toString())
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Yess!")
                        setMessage("Akun Anda berhasil dibuat. login dan coba bagikan story?")
                        setPositiveButton("login") { _, _ ->
                            val intent = Intent(context, WelcomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    showLoading(false)
                    showError(message.message.toString())
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                showError(errorMessage.toString())
                showLoading(false)
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signUpBtn = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, name, nameEdit, email, emailEdit, password, passwordEdit, signUpBtn)
            startDelay = 100
        }.start()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}