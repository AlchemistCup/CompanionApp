package com.example.alchemistcompanion.ui.match

data class BlanksDialogueUiState(
    val nOfBlanks: Int,
    val userInput: String = "",
    val isInputInvalid: Boolean = false
)
