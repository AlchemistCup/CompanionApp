package com.example.alchemistcompanion.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alchemistcompanion.AlchemistCompanionApplication
import com.example.alchemistcompanion.R
import com.example.alchemistcompanion.ui.match.MatchScreen
import com.example.alchemistcompanion.ui.match.MatchViewModel
import com.example.alchemistcompanion.ui.match.MatchViewModelFactory
import com.example.alchemistcompanion.ui.setup.SetupScreen
import com.example.alchemistcompanion.ui.setup.SetupViewModel

enum class Screen(@StringRes val title: Int) {
    Setup(R.string.setup_screen),
    Match(R.string.match_screen)
}

@Composable
fun CompanionApp(
    setupViewModel: SetupViewModel = viewModel(factory = SetupViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Setup.name
    )

    Scaffold() { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Setup.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Setup.name) {
                SetupScreen(
                    viewModel = setupViewModel,
                )
            }
            composable(route = Screen.Match.name) {
                val application = LocalContext.current.applicationContext as AlchemistCompanionApplication
                val viewModel: MatchViewModel = viewModel(
                    factory = MatchViewModelFactory(
                        application.container.matchDataRepository,
                        "Test matchID",
                        "Player1",
                        "Player2"
                    )
                )
                MatchScreen(viewModel = viewModel)
            }
        }
    }
}

private fun cancelMatchAndReset(
    setupViewModel: SetupViewModel,
    navController: NavHostController
) {
    setupViewModel.reset()
    navController.popBackStack(Screen.Setup.name, inclusive = false)
}