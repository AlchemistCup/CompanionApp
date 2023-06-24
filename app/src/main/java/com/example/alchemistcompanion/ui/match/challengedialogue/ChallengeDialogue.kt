package com.example.alchemistcompanion.ui.match.challengedialogue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.R
import com.example.alchemistcompanion.data.DefaultAppContainer
import com.example.alchemistcompanion.ui.common.LoadingIcon
import com.example.alchemistcompanion.ui.match.MatchViewModel
import com.example.alchemistcompanion.ui.match.MatchViewModelFactory
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme

@Composable
fun ChallengeDialogue(
    viewModel: ChallengeDialogueViewModel,
    onSubmission: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color = colorScheme.surface)
    ) {
        when (val dialogueState = uiState.dialogueState) {
            is ChallengeDialogueState.Inactive -> {}
            is ChallengeDialogueState.Loading -> LoadingIcon(modifier.size(200.dp))
            is ChallengeDialogueState.Selecting -> SelectScreen(
                viewModel = viewModel,
                onSubmission = onSubmission,
                modifier = modifier
            )

            is ChallengeDialogueState.Successful -> ResultScreen(
                message = stringResource(R.string.successful_challenge),
                onExit = onExit,
                modifier = modifier
            )

            is ChallengeDialogueState.Unsuccessful -> ResultScreen(
                message = stringResource(R.string.unsuccessful_challenge, dialogueState.penalty),
                onExit = onExit,
                modifier = modifier
            )
        }
    }
}

@Composable
fun SelectScreen(
    viewModel: ChallengeDialogueViewModel,
    onSubmission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(R.string.challenge_select),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))

        Divider(color = colorScheme.outline)
        LazyColumn {
            itemsIndexed(uiState.challengeWords) { idx, word ->
                WordItem(
                    uiState = word,
                    onClick = { viewModel.onWordSelect(idx) },
                    modifier = modifier
                )
                Divider(color = colorScheme.outline)
            }
        }

        Button(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            onClick = onSubmission
        ) {
            Text(
                text = stringResource(R.string.submit),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun WordItem(
    uiState: ChallengeWord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small)
            )
    ) {
        Text(
            text = uiState.word,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = uiState.isSelected,
            onCheckedChange = { onClick() }
        )
    }
}

@Composable
fun ResultScreen(
    message: String,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
           text = message,
           style = MaterialTheme.typography.titleLarge
        )

        Button(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            onClick = onExit
        ) {
            Text(
                text = stringResource(R.string.ok),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun SelectScreenPreview() {
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

        viewModel.challengeDialogueViewModel.setChallengeWords(
            listOf("Hello", "Goodbye", "Qi", "Baff")
        )

        Dialog(onDismissRequest = { /*User cannot dismiss dialogue*/ }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ChallengeDialogue(
                    viewModel = viewModel.challengeDialogueViewModel,
                    onSubmission = viewModel::onChallengeSubmit,
                    onExit = viewModel::onChallengeComplete
                )
            }
        }
    }
}