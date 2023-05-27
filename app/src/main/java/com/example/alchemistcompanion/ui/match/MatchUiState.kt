package com.example.alchemistcompanion.ui.match

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
    val player1: Player,
    val player2: Player,
    val matchState: MatchState = MatchState.Unbegun,
    val isDisconnected: Boolean = false,
    val turnNumber: Int = 0,
    val blankTiles: Int = 0
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
    val isTimerPaused: Boolean,
    val score: Int = 0,
    val remainingTime: Int = 20 * 60 * 1000
)