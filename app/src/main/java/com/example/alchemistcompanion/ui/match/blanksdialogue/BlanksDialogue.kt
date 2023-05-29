package com.example.alchemistcompanion.ui.match.blanksdialogue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.R
import com.example.alchemistcompanion.data.DefaultAppContainer
import com.example.alchemistcompanion.ui.match.MatchViewModel
import com.example.alchemistcompanion.ui.match.MatchViewModelFactory
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme

@Composable
fun BlanksDialogue(
    viewModel: BlanksDialogueViewModel,
    onSubmission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = "${uiState.nOfBlanks} blank tile(s) detected. Please enter their value(s).",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))

            uiState.blankInputs.forEachIndexed { i, blankInput ->
                BlankValueInputField(
                    uiState = blankInput,
                    inputNumber = i,
                    onUserInputChange = { viewModel.onBlankUserUpdate(i, it) },
                    onKeyboardDone = { viewModel.validateBlankInput(i) },
                )
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
            }

            Button(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                onClick = onSubmission
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun BlankValueInputField(
    uiState: BlankInput,
    inputNumber: Int,
    onUserInputChange: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
    ) {
        Text(
            "Blank ${inputNumber + 1}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        OutlinedTextField(
            value = uiState.userInput,
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            onValueChange = onUserInputChange,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = modifier,
            label = {
                if (uiState.isInputInvalid) {
                    Text(stringResource(R.string.enter_blank_error))
                } else {
                    Text(stringResource(R.string.enter_blank_prompt))
                }
            },
            isError = uiState.isInputInvalid,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onKeyboardDone()
                    focusManager.clearFocus()
                }
            )
        )
    }

}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun BlanksDialoguePreview() {
    AlchemistCompanionTheme {
        val appContainer = DefaultAppContainer()
        val viewModel: MatchViewModel = viewModel(
            factory = MatchViewModelFactory(
                appContainer.matchDataRepository,
                "Test matchID",
                "Player1",
                "Player2"
            )
        )
        viewModel.blanksDialogueViewModel.createNewBlanksDialogue(2)

        Dialog(onDismissRequest = { /*Do nothing*/ }) {
            BlanksDialogue(
                viewModel = viewModel.blanksDialogueViewModel,
                onSubmission = viewModel::onBlanksSubmission,
            )
        }
    }
}