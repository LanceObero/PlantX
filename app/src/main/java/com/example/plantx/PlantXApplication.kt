package com.example.plantx

import android.app.Application
import com.example.plantx.api.RetrofitClient

class PlantXApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}