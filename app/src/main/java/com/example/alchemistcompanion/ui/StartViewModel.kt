package com.example.alchemistcompanion.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface MatchStartState {
    data class Success(val matchId: String) : MatchStartState
    object Error : MatchStartState
    object Loading : MatchStartState
}

class StartViewModel : ViewModel() {
    var player1Name by mutableStateOf("")
    var player2Name by mutableStateOf("")

    var matchStartState: MatchStartState? by mutableStateOf(null)

    init {
        reset()
    }

    fun startMatch() {
        matchStartState = MatchStartState.Loading
        viewModelScope.launch {
            // TODO: Server stuff
        }
    }

    fun reset() {
        player1Name = ""
        player2Name = ""
    }
}