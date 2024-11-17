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
import com.coding.aplikasistoryapp.data.pref.UserModel
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
        email.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                val input = s.toString()

                if (input.matches(emailPattern.toRegex())) {
                    email.error = null
                } else {
                    email.error = "Format email tidak valid"
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        password = binding.passwordEditText
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    password.setError("Password tidak boleh kurang dari 8 karakter", null)
                } else {
                    password.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        binding.loginButton.setOnClickListener {
            val emailText = email.text.toString()
            val passText = password.text.toString()
            println("email $emailText")
            println("pass $passText")
            performLogin(emailText, passText)
        }
    }

    private fun performLogin(email: String, password: String) {

        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        loginViewModel.login(email, password) // Panggil login untuk memulai proses

        loginViewModel.loginResult.observe(this) { result ->
            if (result != null) {
                println("Data diterima di Activity: $result")
                if (result.error != true) {
                    val user = result.loginResult
                    if (user != null) {
                        // Simpan sesi pengguna
                        loginViewModel.saveSession(
                            UserModel(user.name ?: "", user.token ?: "", true)
                        )
                        AlertDialog.Builder(this).apply {
                            setTitle("Yess!")
                            setMessage("Anda berhasil login. Sudah tidak sabar berbagi cerita?")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    } else {
                        Toast.makeText(this, "Data login tidak valid", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login gagal ${result.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Gagal login: Tidak ada respons hehe", Toast.LENGTH_SHORT).show()
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