package com.example.alchemistcompanion.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel(
    val matchId: String,
    private val player1Name: String,
    private val player2Name: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MatchUiState(
            Player(player1Name),
            Player(player2Name)
        )
    )
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    init {
        reset()
    }

    fun reset() {
        // TODO: implement
    }
}