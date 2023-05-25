package com.example.alchemistcompanion.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alchemistcompanion.AlchemistCompanionApplication
import com.example.alchemistcompanion.ui.match.MatchScreen
import com.example.alchemistcompanion.ui.match.MatchViewModel
import com.example.alchemistcompanion.ui.match.MatchViewModelFactory
import com.example.alchemistcompanion.ui.setup.SetupScreen
import com.example.alchemistcompanion.ui.setup.SetupViewModel

enum class Screen {
    Setup,
    Match
}

@Composable
fun CompanionApp(
    setupViewModel: SetupViewModel = viewModel(factory = SetupViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val matchIdArg = "matchId"
    val player1NameArg = "player1Name"
    val player2NameArg = "player2Name"

    Scaffold() { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Setup.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Setup.name) {
                SetupScreen(
                    viewModel = setupViewModel,
                    onMatchSetup = { matchId, player1Name, player2Name ->
                        navController.navigate(
                            route = "${Screen.Match.name}/$matchId/$player1Name/$player2Name"
                        )
                    }
                )
            }
            composable(
                route = "${Screen.Match.name}/{$matchIdArg}/{$player1NameArg}/{$player2NameArg}",
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString(matchIdArg)
                val player1Name = backStackEntry.arguments?.getString(player1NameArg)
                val player2Name = backStackEntry.arguments?.getString(player2NameArg)

                if (matchId != null && player1Name != null && player2Name != null) {
                    val application =
                        LocalContext.current.applicationContext as AlchemistCompanionApplication
                    val viewModel: MatchViewModel = viewModel(
                        factory = MatchViewModelFactory(
                            matchDataRepository = application.container.matchDataRepository,
                            matchId = matchId,
                            player1Name = player1Name,
                            player2Name = player2Name
                        )
                    )
                    MatchScreen(viewModel = viewModel)
                } else {
                    throw RuntimeException("Did not receive required arguments from setup (should be impossible")
                }
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