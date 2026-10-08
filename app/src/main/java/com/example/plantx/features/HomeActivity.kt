package com.example.plantx.features

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.plantx.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val bottomNavigation = findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        )

        val preferences = getSharedPreferences(
            "PlantXPrefs",
            MODE_PRIVATE
        )

        val fullName = preferences.getString(
            "full_name",
            "PlantX User"
        )

        welcomeText.text = "Welcome, $fullName!"

        bottomNavigation.selectedItemId = R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    true
                }

                R.id.nav_identify -> {
                    startActivity(
                        Intent(this, IdentifyActivity::class.java)
                    )
                    true
                }

                R.id.nav_history -> {
                    startActivity(
                        Intent(this, HistoryActivity::class.java)
                    )
                    true
                }

                R.id.nav_profile -> {
                    startActivity(
                        Intent(this, ProfileActivity::class.java)
                    )
                    true
                }

                else -> false
            }
        }
    }
}