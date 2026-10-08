package com.example.plantx.Registration

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantx.R
import com.example.plantx.api.RegisterRequest
import com.example.plantx.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RetrofitClient.initialize(applicationContext)

        val fullName = findViewById<EditText>(R.id.etFullName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val backToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        registerButton.setOnClickListener {

            val name = fullName.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString()
            val confirm = confirmPassword.text.toString()

            if (name.isEmpty() ||
                userEmail.isEmpty() ||
                userPassword.isEmpty() ||
                confirm.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (userPassword != confirm) {
                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            registerButton.isEnabled = false

            val request = RegisterRequest(
                full_name = name,
                email = userEmail,
                password = userPassword,
                confirm_password = confirm
            )

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val response = RetrofitClient.api.register(request)

                    val errorMessage =
                        response.errorBody()?.string()

                    withContext(Dispatchers.Main) {

                        registerButton.isEnabled = true

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Account created successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration failed (${response.code()}): $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {

                        registerButton.isEnabled = true

                        Toast.makeText(
                            this@RegisterActivity,
                            "Connection error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        backToLogin.setOnClickListener {
            finish()
        }
    }
}