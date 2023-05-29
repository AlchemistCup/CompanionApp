package com.example.alchemistcompanion.ui.match.challengedialogue

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChallengeDialogueViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(
        ChallengeDialogueUiState(listOf())
    )
    val uiState: StateFlow<ChallengeDialogueUiState> = _uiState.asStateFlow()

    fun setChallengeWords(words: List<String>) {
        _uiState.update {
            ChallengeDialogueUiState(
                challengeWords = words.map { ChallengeWord(it) },
                dialogueState = ChallengeDialogueState.Selecting
            )
        }
    }

    val selectedWords: List<String>
        get() {
            return uiState.value.challengeWords
                .filter { it.isSelected }
                .map { it.word }
        }

    fun reset() {
        _uiState.update {
            ChallengeDialogueUiState(
                challengeWords = listOf(),
                dialogueState = ChallengeDialogueState.Inactive
            )
        }
    }

    fun onWordSelect(index: Int) {
        if (index !in 0..uiState.value.challengeWords.lastIndex)
            throw IndexOutOfBoundsException("Index $index must be in range [0, ${uiState.value.challengeWords.lastIndex}]")

        val currWord = uiState.value.challengeWords[index]
        val newWord = currWord.copy(isSelected = !currWord.isSelected)
        updateChallengeWord(index, newWord)
    }

    fun updateState(state: ChallengeDialogueState) {
        _uiState.update { currentState ->
            currentState.copy(dialogueState = state)
        }
    }


    private fun updateChallengeWord(index: Int, newChallengeWord: ChallengeWord) {
        val newChallengeWords = uiState.value.challengeWords.toMutableList()
        newChallengeWords[index] = newChallengeWord
        _uiState.update { currentState ->
            currentState.copy(challengeWords = newChallengeWords)
        }
    }
}