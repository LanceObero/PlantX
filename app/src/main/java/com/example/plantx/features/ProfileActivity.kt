package com.example.plantx.features

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.plantx.MainActivity
import com.example.plantx.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvName = findViewById<TextView>(R.id.tvName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val preferences = getSharedPreferences(
            "PlantXPrefs",
            MODE_PRIVATE
        )

        val fullName = preferences.getString(
            "full_name",
            "PlantX User"
        )

        val email = preferences.getString(
            "email",
            ""
        )

        tvName.text = fullName
        tvEmail.text = email

        btnLogout.setOnClickListener {

            preferences.edit()
                .clear()
                .apply()

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}