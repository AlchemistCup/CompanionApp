package com.example.alchemistcompanion.ui.match

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.R
import com.example.alchemistcompanion.data.DefaultAppContainer
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme
import kotlinx.coroutines.delay
import kotlin.math.ceil

@Composable
fun FormattedTimeDisplay(
    timeInMs: Int
) {
    var normalisedTime = ceil(timeInMs.toDouble() / 1000).toInt()
    val overTime = normalisedTime < 0
    normalisedTime = if (overTime) -normalisedTime else normalisedTime
    val minutes = (normalisedTime / 60).toString().padStart(2, '0')
    val seconds = (normalisedTime % 60).toString().padStart(2, '0')

    Text(
        text = "${if (overTime) "-" else ""}$minutes:$seconds",
        style = typography.displayLarge
    )
}

@Composable
fun EnabledButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    label: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            if (!isEnabled) colorScheme.secondary else colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = typography.displayMedium
        )
    }
}

@Composable
fun MatchTimer(
    playerId: PlayerId,
    uiState: MatchUiState,
    onPauseClick: (PlayerId) -> Unit,
    onTimerUpdate: (PlayerId, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val player = uiState.getPlayerState(playerId)
    val isEnabled = !player.isTimerPaused &&
            uiState.matchState == MatchState.InProgress

    LaunchedEffect(isEnabled) {
        while (true) {
            delay(100)
            if (isEnabled) {
                onTimerUpdate(playerId, 100)
            }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        FormattedTimeDisplay(timeInMs = player.remainingTime)
        Spacer(modifier = Modifier.height(16.dp))
        EnabledButton(
            onClick = { onPauseClick(playerId) },
            isEnabled = isEnabled,
            label = "End turn"
        )
    }
}

@Composable
fun PlayerCard(
    playerId: PlayerId,
    uiState: MatchUiState,
    onPauseClick: (PlayerId) -> Unit,
    onTimerUpdate: (PlayerId, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val player = uiState.getPlayerState(playerId)

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = player.name,
                style = typography.displaySmall,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )

            Text(
                text = "Score: ${player.score}",
                style = typography.displayLarge,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            )

            MatchTimer(
                playerId = playerId,
                uiState = uiState,
                onPauseClick = onPauseClick,
                onTimerUpdate = onTimerUpdate,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            )
        }
    }
}

@Composable
fun UtilityButtons(
    uiState: MatchUiState,
    onChallenge: () -> Unit,
    onHold: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canChallenge = true
    val canHold = true

    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(dimensionResource(R.dimen.padding_small))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnabledButton(
            onClick = onChallenge,
            isEnabled = canChallenge,
            label = "Challenge",
            modifier = buttonModifier
        )

        EnabledButton(
            onClick = onHold,
            isEnabled = canHold,
            label = "Hold",
            modifier = buttonModifier
        )

        Button(
            onClick = onPause,
            enabled = uiState.matchState != MatchState.Finished,
            colors = ButtonDefaults.buttonColors(
                when (uiState.matchState) {
                    MatchState.Finished -> colorScheme.secondary
                    else -> colorScheme.primary
                }
            ),
            modifier = buttonModifier
        ) {
            Text(
                text = when (uiState.matchState) {
                    MatchState.Unbegun -> "Start"
                    MatchState.InProgress, MatchState.Finished -> "Pause"
                    MatchState.Paused -> "Resume"
                },
                style = typography.displayMedium
            )
        }
    }

}

@Composable
fun SendBlanksDialogue(
    uiState: BlanksDialogueUiState,
    onUserInputChange: (String) -> Unit,
    onSubmission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = colorScheme.surface)
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
                style = typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = uiState.userInput,
                singleLine = true,
                shape = shapes.large,
                onValueChange = onUserInputChange,
                textStyle = typography.titleMedium,
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
                    style = typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun MatchScreen(
    viewModel: MatchViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.alchemist_logo),
            contentDescription = stringResource(R.string.alchemist_logo),
            modifier = Modifier
                .height(150.dp)
                .padding(dimensionResource(R.dimen.padding_small))
        )

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val playerCardModifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_medium))

            PlayerCard(
                playerId = PlayerId.Player1,
                uiState = uiState,
                onPauseClick = viewModel::endTurn,
                onTimerUpdate = viewModel::decrementRemainingTime,
                modifier = playerCardModifier
            )

            UtilityButtons(
                uiState = uiState,
                onChallenge = viewModel::onChallenge,
                onHold = viewModel::onHold,
                onPause = viewModel::toggleMatchState,
                modifier = Modifier.weight(1f)
            )

            PlayerCard(
                playerId = PlayerId.Player2,
                uiState = uiState,
                onPauseClick = viewModel::endTurn,
                onTimerUpdate = viewModel::decrementRemainingTime,
                modifier = playerCardModifier
            )
        }
    }

    val blanksUiState by viewModel.blanksUiState.collectAsState()
    if (blanksUiState.nOfBlanks > 0) {
        Dialog(onDismissRequest = { /*Do nothing*/ }) {
            SendBlanksDialogue(
                uiState = blanksUiState,
                onUserInputChange = viewModel::onBlanksUserUpdate,
                onSubmission = viewModel::onBlanksSubmission,
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.7f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun MatchScreenPreview() {
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
        MatchScreen(viewModel)
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

        SendBlanksDialogue(
            uiState = blanksUiState,
            onUserInputChange = viewModel::onBlanksUserUpdate,
            onSubmission = viewModel::onBlanksSubmission,
            modifier = Modifier.fillMaxSize(.7f)
        )
    }
}