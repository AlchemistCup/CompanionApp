package com.example.alchemistcompanion.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.R
import com.example.alchemistcompanion.data.DefaultAppContainer
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme

@Composable
fun BlanksDialogue(
    uiState: BlanksDialogueUiState,
    onUserInputChange: (String) -> Unit,
    onSubmission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = "${uiState.nOfBlanks} blank tile(s) detected. Please enter their value(s) in reading order.",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = uiState.userInput,
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                onValueChange = onUserInputChange,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    if (uiState.isInputInvalid) {
                        Text("Expected ${uiState.nOfBlanks} letter(s)")
                    } else {
                        Text("Enter blank value(s)")
                    }
                },
                isError = uiState.isInputInvalid,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmission() }
                )
            )
            Button(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                onClick = onSubmission
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
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
        viewModel.createNewBlanksDialogue(1)

        val blanksUiState by viewModel.blanksUiState.collectAsState()

        Dialog(onDismissRequest = { /*Do nothing*/ }) {
            BlanksDialogue(
                uiState = blanksUiState,
                onUserInputChange = viewModel::onBlanksUserUpdate,
                onSubmission = viewModel::onBlanksSubmission,
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.6f)
            )
        }
    }
}