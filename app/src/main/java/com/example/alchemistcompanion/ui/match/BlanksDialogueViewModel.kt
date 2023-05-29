package com.example.alchemistcompanion.ui.match

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BlanksDialogueViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(
        BlanksDialogueUiState(0)
    )
    val uiState: StateFlow<BlanksDialogueUiState> = _uiState.asStateFlow()

    val blankValues: List<String>
        get() {
            return uiState.value.blankInputs.map { it.userInput }
        }

    val isValid: Boolean
        get() {
            return uiState.value.blankInputs.all { !it.isInputInvalid }
        }

    fun createNewBlanksDialogue(nOfBlanks: Int) {
        if (nOfBlanks !in 1..2)
            throw IllegalArgumentException("Cannot create new blanks dialogue with invalid number of blanks $nOfBlanks")

        _uiState.update {
            BlanksDialogueUiState(nOfBlanks)
        }
    }

    fun onBlankUserUpdate(index: Int, userInput: String) {
        if (index !in 0 until uiState.value.nOfBlanks)
            throw IndexOutOfBoundsException("Index $index must be in range [0, ${uiState.value.nOfBlanks - 1}]")

        val currBlank = uiState.value.blankInputs[index]
        val newBlank = currBlank.copy(userInput = userInput)
        updateBlankInput(index, newBlank)
    }

    fun validateBlankInput(index: Int) {
        if (index !in 0 until uiState.value.nOfBlanks)
            throw IndexOutOfBoundsException("Index $index must be in range [0, ${uiState.value.nOfBlanks - 1}]")

        fun String.isAlpha() = all { it.isLetter() }
        val currBlank = uiState.value.blankInputs[index]
        val isInputValid = currBlank.userInput.length == 1 && currBlank.userInput.isAlpha()
        val newBlank = currBlank.copy(isInputInvalid = !isInputValid)
        updateBlankInput(index, newBlank)
    }

    fun reset() {
        _uiState.update {
            BlanksDialogueUiState(0)
        }
    }

    private fun updateBlankInput(index: Int, newBlankInput: BlankInput) {
        val newBlanksList = uiState.value.blankInputs.toMutableList()
        newBlanksList[index] = newBlankInput
        _uiState.update { currentState ->
            currentState.copy(blankInputs = newBlanksList)
        }
    }
}