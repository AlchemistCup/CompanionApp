package com.example.alchemistcompanion

import android.app.Application
import com.example.alchemistcompanion.data.AppContainer
import com.example.alchemistcompanion.data.DefaultAppContainer

class AlchemistCompanionApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}