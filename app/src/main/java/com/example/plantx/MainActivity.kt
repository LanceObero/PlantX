package com.example.plantx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.plantx.Registration.RegisterActivity
import com.example.plantx.api.LoginRequest
import com.example.plantx.api.RetrofitClient
import com.example.plantx.features.HomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val createAccount = findViewById<TextView>(R.id.tvCreateAccount)

        loginButton.setOnClickListener {

            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter your email and password",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            loginButton.isEnabled = false

            lifecycleScope.launch {

                try {

                    val response = RetrofitClient.api.login(
                        LoginRequest(
                            email = userEmail,
                            password = userPassword
                        )
                    )

                    if (response.isSuccessful) {

                        val loginResponse = response.body()

                        if (loginResponse != null) {

                            getSharedPreferences(
                                "PlantXPrefs",
                                MODE_PRIVATE
                            ).edit()
                                .putString(
                                    "access_token",
                                    loginResponse.access
                                )
                                .putString(
                                    "refresh_token",
                                    loginResponse.refresh
                                )
                                .putString(
                                    "email",
                                    loginResponse.email
                                )
                                .putString(
                                    "full_name",
                                    loginResponse.full_name
                                )
                                .apply()

                            Toast.makeText(
                                this@MainActivity,
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(
                                this@MainActivity,
                                HomeActivity::class.java
                            )

                            startActivity(intent)

                            finish()
                        }

                    } else {

                        Toast.makeText(
                            this@MainActivity,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {

                    Toast.makeText(
                        this@MainActivity,
                        "Connection error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                } finally {

                    loginButton.isEnabled = true
                }
            }
        }

        createAccount.setOnClickListener {

            val intent = Intent(
                this,
                RegisterActivity::class.java
            )

            startActivity(intent)
        }
    }
}