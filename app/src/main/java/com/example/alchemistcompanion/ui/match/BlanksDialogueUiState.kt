package com.example.alchemistcompanion.ui.match

data class BlanksDialogueUiState(
    val nOfBlanks: Int,
    val blankInputs: List<BlankInput> = List(nOfBlanks) { BlankInput() }
)

data class BlankInput(
    val userInput: String = "",
    val isInputInvalid: Boolean = false
)
