package com.example.alchemistcompanion.ui.match

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BlanksDialogueViewModel() : ViewModel() {
    private val _blanksUiState = MutableStateFlow(
        BlanksDialogueUiState(0)
    )
    val blanksUiState: StateFlow<BlanksDialogueUiState> = _blanksUiState.asStateFlow()

    fun createNewBlanksDialogue(nOfBlanks: Int) {
        _blanksUiState.update {
            BlanksDialogueUiState(nOfBlanks)
        }
    }

    fun onBlankUserUpdate(index: Int, userInput: String) {
        val currBlank = blanksUiState.value.blankInputs[index]
        val newBlank = currBlank.copy(userInput = userInput)
        updateBlankInput(index, newBlank)
    }

    fun validateBlankInput(index: Int) {
        fun String.isAlpha() = all { it.isLetter() }
        val currBlank = blanksUiState.value.blankInputs[index]
        val isInputValid = currBlank.userInput.length == 1 && currBlank.userInput.isAlpha()
        val newBlank = currBlank.copy(isInputInvalid = !isInputValid)
        updateBlankInput(index, newBlank)
    }

    val blankValues: List<String>
        get() {
             return blanksUiState.value.blankInputs.map { it.userInput }
        }

    val isValid: Boolean
        get() {
            return blanksUiState.value.blankInputs.all { !it.isInputInvalid }
        }

    private fun updateBlankInput(index: Int, newBlankInput: BlankInput) {
        val newBlanksList = blanksUiState.value.blankInputs.toMutableList()
        newBlanksList[index] = newBlankInput
        _blanksUiState.update { currentState ->
            currentState.copy(blankInputs = newBlanksList)
        }
    }
}