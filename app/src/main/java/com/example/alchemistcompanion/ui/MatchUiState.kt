package com.example.alchemistcompanion.ui

enum class PlayerId {
    Player1,
    Player2
}

enum class MatchState {
    Unbegun,
    InProgress,
    Paused,
    Finished
}
data class MatchUiState (
    private val player1: Player,
    private val player2: Player,
    val matchState: MatchState = MatchState.Unbegun,
    val isDisconnected: Boolean = false,
) {
    fun getPlayerState(playerId: PlayerId): Player {
        return when (playerId) {
            PlayerId.Player1 -> player1
            PlayerId.Player2 -> player2
        }
    }
}

data class Player(
    val name: String,
    val score: Int = 0,
    val remainingTime: Int = 20 * 60 * 1000,
    val isTimerPaused: Boolean = true
)