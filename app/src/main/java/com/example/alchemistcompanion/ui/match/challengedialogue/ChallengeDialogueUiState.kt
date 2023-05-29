package com.example.alchemistcompanion.ui.match.challengedialogue

sealed interface ChallengeDialogueState {
    object Inactive : ChallengeDialogueState
    object Loading : ChallengeDialogueState
    object Selecting : ChallengeDialogueState
    object Successful : ChallengeDialogueState
    data class Unsuccessful(val penalty: Int) : ChallengeDialogueState
}

data class ChallengeDialogueUiState(
    val challengeWords: List<ChallengeWord>,
    val dialogueState: ChallengeDialogueState = ChallengeDialogueState.Inactive
)

data class ChallengeWord(
    val word: String,
    val isSelected: Boolean = false
)