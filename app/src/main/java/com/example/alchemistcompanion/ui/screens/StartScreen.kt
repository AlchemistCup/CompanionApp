package com.example.alchemistcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alchemistcompanion.R
import com.example.alchemistcompanion.ui.StartViewModel
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme

@Composable
fun StartScreen(
    viewModel: StartViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_large),
                    bottom = dimensionResource (id = R.dimen.padding_large)
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerField(
                playerName = viewModel.player1Name,
                onPlayerNameChanged = { viewModel.player1Name = it },
                prompt = stringResource(R.string.player_1)
            )
            PlayerField(
                playerName = viewModel.player2Name,
                onPlayerNameChanged = { viewModel.player2Name = it },
                prompt = stringResource(R.string.player_2)
            )
        }

        Button(
            onClick = { viewModel.startMatch() }
        ) {
            Text(
                text = stringResource(R.string.setup_match),
                style = typography.displaySmall
            )
        }
    }

}

@Composable
fun PlayerField(
    playerName: String,
    onPlayerNameChanged: (String) -> Unit,
    prompt: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                text = prompt,
                style = typography.displayMedium,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
            )

            OutlinedTextField(
                value = playerName,
                singleLine = true,
                shape = shapes.large,
                onValueChange = onPlayerNameChanged,
                textStyle = typography.titleMedium,
                label = { Text(stringResource(id = R.string.player_name_prompt)) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun StartScreenPreview() {
    AlchemistCompanionTheme() {
        StartScreen()
    }
}