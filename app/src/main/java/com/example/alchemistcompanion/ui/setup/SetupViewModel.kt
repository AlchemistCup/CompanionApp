package com.example.alchemistcompanion.ui.setup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alchemistcompanion.AlchemistCompanionApplication
import com.example.alchemistcompanion.data.MatchDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed interface MatchStartState {
    data class Success(val matchId: String) : MatchStartState
    data class Error(val reason: String) : MatchStartState
    object Loading : MatchStartState
}

class SetupViewModel(
    private val matchDataRepository: MatchDataRepository
) : ViewModel() {
    private val TAG = "SetupViewModel"
    // Potentially move these out of the view model?
    var player1Name by mutableStateOf("")
    var player2Name by mutableStateOf("")

    var matchStartState: MatchStartState? by mutableStateOf(null)

    init {
        reset()
    }

    fun startMatch(onMatchSetup: (String, String, String) -> Unit) {
        matchStartState = MatchStartState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            matchStartState = try {
                val result = matchDataRepository.setupMatch(player1Name, player2Name)
                if (result.success) {
                    MatchStartState.Success(result.body!!.matchId)
                }
                else {
                    MatchStartState.Error(
                        result.error!!
                    )
                }
            } catch (e: IOException) {
                Log.d(TAG, "$e")
                // This can also be a java.net.ProtocolException: unexpected end of stream, but apparently this is a bug in the android studio emulator (https://stackoverflow.com/a/61685212)
                MatchStartState.Error("Unable to connect to server")
            } catch (e: HttpException) {
                Log.d(TAG, "$e")
                MatchStartState.Error("$e")
            }

            // UI changes must be performed in Main thread
            withContext(Dispatchers.Main) {
                if (matchStartState is MatchStartState.Success) {
                    val matchId = (matchStartState as MatchStartState.Success).matchId
                    Log.d(
                        TAG,
                        "Calling OnMatchSetup with matchId=$matchId, player1Name=$player1Name, player2Name=$player2Name"
                    )
                    onMatchSetup(matchId, player1Name, player2Name)
                }
            }
        }
    }

    fun reset() {
        player1Name = ""
        player2Name = ""
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AlchemistCompanionApplication)
                val matchDataRepository = application.container.matchDataRepository
                SetupViewModel(matchDataRepository)
            }
        }
    }
}