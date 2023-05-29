package com.example.alchemistcompanion.ui.match

enum class MatchState {
    Unbegun,
    InProgress,
    Paused,
    Finished
}

enum class PlayerId {
    Player1,
    Player2
}

data class Player(
    val name: String,
    val isTimerPaused: Boolean,
    val score: Int = 0,
    val remainingTime: Int = 20 * 60 * 1000
)
data class MatchUiState (
    val player1: Player,
    val player2: Player,
    val matchState: MatchState = MatchState.Unbegun,
    val isDisconnected: Boolean = false,
    val turnNumber: Int = 0,
    val hasChallenged: Boolean = false
) {
    fun getPlayerState(playerId: PlayerId): Player {
        return when (playerId) {
            PlayerId.Player1 -> player1
            PlayerId.Player2 -> player2
        }
    }

    val inactivePlayerId: PlayerId
        get() {
            if (matchState == MatchState.Unbegun || matchState == MatchState.Finished)
                throw IllegalStateException("Match state must be InProgress or Paused to get inactive playerId")

            return if (player1.isTimerPaused) PlayerId.Player1 else PlayerId.Player2
        }
}