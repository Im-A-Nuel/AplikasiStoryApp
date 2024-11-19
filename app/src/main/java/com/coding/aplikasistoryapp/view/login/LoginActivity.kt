package com.coding.aplikasistoryapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.coding.aplikasistoryapp.R
import com.coding.aplikasistoryapp.data.pref.UserModel
import com.coding.aplikasistoryapp.data.pref.UserPreference
import com.coding.aplikasistoryapp.data.pref.dataStore
import com.coding.aplikasistoryapp.databinding.ActivityLoginBinding
import com.coding.aplikasistoryapp.view.ViewModelFactory
import com.coding.aplikasistoryapp.view.main.MainActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        email = binding.emailEditText
        password = binding.passwordEditText

        email.addTextChangedListener(inputTextWatcher)
        password.addTextChangedListener(inputTextWatcher)

        binding.loginButton.setOnClickListener {
            val emailText = email.text.toString()
            val passText = password.text.toString()
            performLogin(emailText, passText)
        }

        binding.loginButton.isEnabled = false
    }

    private val inputTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkFormValidity()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun checkFormValidity() {
        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
        val isPasswordValid = passwordInput.length >= 8

        binding.loginButton.isEnabled = isEmailValid && isPasswordValid

        if (!isEmailValid && emailInput.isNotEmpty()) {
            email.error = getString(R.string.invalid_format_email)
        } else {
            email.error = null
        }

        if (!isPasswordValid && passwordInput.isNotEmpty()) {
            password.error = getString(R.string.invalid_format_password)
        } else {
            password.error = null
        }
    }

    private fun performLogin(email: String, password: String) {
        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        loginViewModel.login(email, password)

        loginViewModel.loginResult.observe(this) { result ->
            if (result != null) {
                if (result.error != true) {
                    val user = result.loginResult
                    if (user != null) {
                        loginViewModel.saveSession(
                            UserModel(user.userId ?: "", user.name ?: "", user.token ?: "", true)
                        )
                        AlertDialog.Builder(this).apply {
                            setTitle("Yess!")
                            setMessage(getString(R.string.message_login))
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.login_valid), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login gagal ${result.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.login_valid), Toast.LENGTH_SHORT).show()
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
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val loginBtn = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, message, email, emailEdit, password, passwordEdit, loginBtn)
            startDelay = 100
        }.start()
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
