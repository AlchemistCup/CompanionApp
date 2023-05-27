package com.example.alchemistcompanion.ui.match

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alchemistcompanion.data.MatchDataRepository
import com.example.alchemistcompanion.network.ServerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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

    private val _blanksUiState = MutableStateFlow(
        BlanksDialogueUiState(0)
    )
    val blanksUiState: StateFlow<BlanksDialogueUiState> = _blanksUiState.asStateFlow()

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
        val currentTurn = uiState.value.turnNumber
        _uiState.update { currentState ->
            currentState.copy(
                player1 = invertTimer(PlayerId.Player1),
                player2 = invertTimer(PlayerId.Player2),
                turnNumber = currentTurn + 1
            )
        }

        if (!uiState.value.isDisconnected) {
            viewModelScope.launch(Dispatchers.IO) {
                makeServerRequest(
                    request = { matchDataRepository.endTurn(
                        matchId = matchId,
                        turnNumber = currentTurn,
                        playerTime = uiState.value.getPlayerState(playerId).remainingTime
                    ) },
                    onSuccess = { result ->
                        Log.d(TAG, "Received end turn response $result")
                        val currentPlayer = uiState.value.getPlayerState(playerId)
                        val updatedPlayer =
                            currentPlayer.copy(score = currentPlayer.score + result.score)
                        updatePlayerState(playerId, updatedPlayer)
                        if (result.blanks > 0) {
                            createNewBlanksDialogue(result.blanks)
                        }
                    }
                )
            }
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

    fun createNewBlanksDialogue(nOfBlanks: Int) {
        _blanksUiState.update {
            BlanksDialogueUiState(nOfBlanks)
        }
    }

    fun onBlanksUserUpdate(userInput: String) {
        _blanksUiState.update { currentState ->
            currentState.copy(userInput = userInput)
        }
    }

    fun onBlanksSubmission() {
        fun String.isAlpha() = all { it.isLetter() }
        val isInputValid =
            blanksUiState.value.userInput.length == blanksUiState.value.nOfBlanks
            && blanksUiState.value.userInput.isAlpha()

        _blanksUiState.update { currentState ->
            currentState.copy(isInputInvalid = !isInputValid)
        }

        if (isInputValid) {
            viewModelScope.launch(Dispatchers.IO) {
                makeServerRequest(
                    request = { matchDataRepository.sendBlanks(
                        matchId = matchId,
                        turnNumber = uiState.value.turnNumber - 1, // Always submitting blanks for previous turn
                        blankValues = blanksUiState.value.userInput
                    )},
                    onSuccess = {}
                )
                // Reset blanks dialogue regardless of success / failure on server side to allow match to progress on server error
                createNewBlanksDialogue(0)
            }
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

    private suspend fun <T> makeServerRequest(
        request: suspend () -> ServerResponse<T>,
        onSuccess: (T) -> Unit
    ) {
        try {
            val result = request()
            if (result.success) {
                onSuccess(result.body!!)
            } else {
                Log.d(TAG, "Received end turn error ${result.error}")
                onDisconnect(result.error!!)
            }
        } catch (e: IOException) {
            Log.d(TAG, "$e")
            onDisconnect("Unable to connect to server")
        } catch (e: HttpException) {
            Log.d(TAG, "$e")
            onDisconnect("$e")
        }
    }

    private fun onDisconnect(reason: String) {
        Log.d(TAG, "Switching to offline mode ($reason)")
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