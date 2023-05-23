package com.example.alchemistcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.ui.StartViewModel
import com.example.alchemistcompanion.ui.screens.StartScreen
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlchemistCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val viewModel: StartViewModel = viewModel(factory = StartViewModel.Factory)
                    StartScreen(viewModel)
                }
            }
        }
    }
}