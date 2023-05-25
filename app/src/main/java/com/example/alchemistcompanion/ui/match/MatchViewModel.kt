package com.example.alchemistcompanion.ui.match

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alchemistcompanion.data.MatchDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MatchViewModel(
    private val matchDataRepository: MatchDataRepository,
    private val matchId: String,
    player1Name: String,
    player2Name: String
) : ViewModel() {
    private val TAG = "MatchViewModel"

    private val _uiState = MutableStateFlow(
        MatchUiState(
            player1 = Player(
                name = player1Name,
                isTimerPaused = false // Player 1 always starts
            ),
            player2 = Player(
                name = player2Name,
                isTimerPaused = true
            )
        )
    )

    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    fun decrementRemainingTime(player: PlayerId, timeInMs: Int) {
        val currentTime = uiState.value.getPlayerState(player).remainingTime
        val updatedPlayer = uiState.value.getPlayerState(player).copy(remainingTime = currentTime - timeInMs)
        updatePlayerState(player, updatedPlayer)
    }

    fun endTurn(playerId: PlayerId) {
        fun invertTimer(playerId: PlayerId): Player {
            val timerState = uiState.value.getPlayerState(playerId).isTimerPaused
            return uiState.value.getPlayerState(playerId).copy(isTimerPaused = !timerState)
        }
        Log.d(TAG, "$playerId ended their turn")
        _uiState.update { currentState ->
            currentState.copy(
                player1 = invertTimer(PlayerId.Player1),
                player2 = invertTimer(PlayerId.Player2)
            )
        }
    }

    fun toggleMatchState() {
        _uiState.update { currentState ->
            currentState.copy(matchState = when(uiState.value.matchState) {
                MatchState.Unbegun -> MatchState.InProgress
                MatchState.InProgress -> MatchState.Paused
                MatchState.Paused -> MatchState.InProgress
                MatchState.Finished -> throw AssertionError("Cannot toggle match state when match is finished")
            })
        }
    }

    fun onHold() {
        Log.d(TAG, "Hold called")
    }

    fun onChallenge() {
        Log.d(TAG, "Challenge called")
    }

    private fun updatePlayerState(playerId: PlayerId, updatedPlayer: Player) {
        _uiState.update { currentState ->
            when (playerId) {
                PlayerId.Player1 -> currentState.copy(player1 = updatedPlayer)
                PlayerId.Player2 -> currentState.copy(player2 = updatedPlayer)
            }
        }
    }
}

class MatchViewModelFactory(
    private val matchDataRepository: MatchDataRepository,
    private val matchId: String,
    private val player1Name: String,
    private val player2Name: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchViewModel::class.java))
            return MatchViewModel(matchDataRepository, matchId, player1Name, player2Name) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}