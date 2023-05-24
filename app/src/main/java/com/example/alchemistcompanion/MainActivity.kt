package com.example.alchemistcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.ui.setup.SetupViewModel
import com.example.alchemistcompanion.ui.setup.SetupScreen
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
                    val viewModel: SetupViewModel = viewModel(factory = SetupViewModel.Factory)
                    SetupScreen(viewModel)
                }
            }
        }
    }
}