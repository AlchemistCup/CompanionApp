package com.example.alchemistcompanion.ui.match

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alchemistcompanion.data.MatchDataRepository
import com.example.alchemistcompanion.network.ServerResponse
import com.example.alchemistcompanion.ui.match.blanksdialogue.BlanksDialogueViewModel
import com.example.alchemistcompanion.ui.match.challengedialogue.ChallengeDialogueState
import com.example.alchemistcompanion.ui.match.challengedialogue.ChallengeDialogueViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface ServerConnectionState {
    object Success : ServerConnectionState
    data class Error(val reason: String) : ServerConnectionState
    object Loading : ServerConnectionState
}

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

    var serverConnectionState: ServerConnectionState by mutableStateOf(ServerConnectionState.Success)
        //sprivate set

    val blanksDialogueViewModel = BlanksDialogueViewModel()
    val challengeDialogueViewModel = ChallengeDialogueViewModel()

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
                turnNumber = currentTurn + 1,
                hasChallenged = false
            )
        }

        if (serverConnectionState is ServerConnectionState.Success) {
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
                            currentPlayer.copy(score = currentPlayer.score + result.score + (result.endGameBonus ?: 0))
                        updatePlayerState(playerId, updatedPlayer)
                        if (result.blanks > 0) {
                            blanksDialogueViewModel.createNewBlanksDialogue(result.blanks)
                        }

                        if (result.endGameBonus != null) {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    matchState = MatchState.Finished
                                )
                            }
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

    fun onBlanksSubmission() {
        for (i in 0 until blanksDialogueViewModel.uiState.value.nOfBlanks) {
            blanksDialogueViewModel.validateBlankInput(i)
        }

        if (blanksDialogueViewModel.isValid) {
            viewModelScope.launch(Dispatchers.IO) {
                makeServerRequest(
                    request = { matchDataRepository.sendBlanks(
                        matchId = matchId,
                        turnNumber = uiState.value.turnNumber - 1, // Always submitting blanks for previous turn
                        blankValues = blanksDialogueViewModel.blankValues
                    )},
                    onSuccess = {}
                )
                // Reset blanks dialogue regardless of success / failure on server side to allow match to progress on server error
                blanksDialogueViewModel.reset()
            }
        }
    }

    fun onChallenge() {
        if (uiState.value.matchState == MatchState.InProgress)
            toggleMatchState()

        _uiState.update { currentState ->
            currentState.copy(hasChallenged = true)
        }

        challengeDialogueViewModel.updateState(ChallengeDialogueState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            makeServerRequest(
                request = { matchDataRepository.getChallengeableWords(
                    matchId = matchId,
                    turnNumber = uiState.value.turnNumber - 1
                )},
                onSuccess = { result ->
                    Log.d(TAG, "Received getChallengeableWords response $result")
                    if (result.words.isNotEmpty()) {
                        challengeDialogueViewModel.setChallengeWords(result.words)
                    } else {
                        val msg = "Server provided no challengeable words"
                        Log.d(TAG, msg)
                        onDisconnect(msg)
                    }
                }
            )
        }
    }

    fun onChallengeSubmit() {
        val words = challengeDialogueViewModel.selectedWords
        if (words.isEmpty())
            throw IllegalStateException("At least one word must be selected before submitting challenge")

        challengeDialogueViewModel.updateState(ChallengeDialogueState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            makeServerRequest(
                request = { matchDataRepository.challenge(
                    matchId = matchId,
                    turnNumber = uiState.value.turnNumber,
                    words = words
                )},
                onSuccess = { result ->
                    val opponentId = uiState.value.inactivePlayerId
                    val currOpponent = uiState.value.getPlayerState(opponentId)
                    val newOpponent = if (result.successful) {
                        challengeDialogueViewModel.updateState(ChallengeDialogueState.Successful)
                        currOpponent.copy(
                            score = currOpponent.score - result.undoneMoveScore
                        )
                    } else {
                        challengeDialogueViewModel.updateState(ChallengeDialogueState.Unsuccessful(result.challengerPenalty))
                        currOpponent.copy(
                            score = currOpponent.score + result.challengerPenalty
                        )
                    }
                    updatePlayerState(opponentId, newOpponent)
                }
            )
        }
    }

    fun onChallengeComplete() {
        challengeDialogueViewModel.reset()
        if (uiState.value.matchState == MatchState.Paused)
            toggleMatchState()
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
        serverConnectionState = ServerConnectionState.Loading
        try {
            val result = request()
            if (result.success) {
                serverConnectionState = ServerConnectionState.Success
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
        serverConnectionState = ServerConnectionState.Error(reason)
        challengeDialogueViewModel.reset()
        blanksDialogueViewModel.reset()
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