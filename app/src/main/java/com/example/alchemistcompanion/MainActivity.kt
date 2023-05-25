package com.example.alchemistcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alchemistcompanion.ui.CompanionApp
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlchemistCompanionTheme {
                // A surface container using the 'background' color from the theme
                CompanionApp()
            }
        }
    }
}