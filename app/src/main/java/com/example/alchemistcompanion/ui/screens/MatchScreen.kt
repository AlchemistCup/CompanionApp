package com.example.alchemistcompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme
import kotlinx.coroutines.delay
import kotlin.math.ceil

/*
NOTES:
// Use to restart app
fun triggerRestart(context: Activity) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    if (context is Activity) {
        (context as Activity).finish()
    }
    Runtime.getRuntime().exit(0)
}

- remember = value preserved across recompositions
- rememberSaveable = value preserved across recompositions *and* configuration changes (app is destroyed)
- Use ViewModel to abstract all this crap away

 */

@Composable
fun FormattedTimeDisplay(
    remainingMs: Int
) {
    var normalisedTime = ceil(remainingMs.toDouble() / 1000).toInt()
    val overTime = normalisedTime < 0
    normalisedTime = if (overTime) -normalisedTime else normalisedTime
    val minutes = (normalisedTime / 60).toString().padStart(2, '0')
    val seconds = (normalisedTime % 60).toString().padStart(2, '0')

    Text(text = "${if (overTime) "-" else ""}$minutes:$seconds")
}

@Composable
fun MatchTimer(
    totalSeconds: Int,
    isPaused: Boolean,
    onPauseClick: () -> Unit
) {
    var remainingMs by rememberSaveable { mutableStateOf(totalSeconds * 1000) }
    val hasStarted = remainingMs != totalSeconds * 1000

    LaunchedEffect(isPaused) {
        while (true) {
            delay(100)
            if (!isPaused) {
                remainingMs -= 100
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FormattedTimeDisplay(remainingMs = remainingMs)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onPauseClick,
            colors = ButtonDefaults.buttonColors(
                if (isPaused && hasStarted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = if (!hasStarted) "Start" else if (isPaused) "Resume" else "Pause")
        }
    }
}

//@Composable
//fun PlayerCard(
//
//) {
//
//}

@Composable
fun MatchScreen(
    totalSeconds: Int
) {
    var isPaused by rememberSaveable { mutableStateOf(true) }
    MatchTimer(totalSeconds = totalSeconds, isPaused = isPaused) {
        isPaused = !isPaused
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AlchemistCompanionTheme {
        MatchScreen(10)
    }
}