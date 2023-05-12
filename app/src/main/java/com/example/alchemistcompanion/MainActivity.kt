package com.example.alchemistcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alchemistcompanion.ui.theme.AlchemistCompanionTheme
import kotlinx.coroutines.delay
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlchemistCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    FormattedTimeDisplay(1200)
                }
            }
        }
    }
}

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
    var remainingMs by remember { mutableStateOf(totalSeconds * 1000) }
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
                backgroundColor = if (isPaused && hasStarted) Color.Gray else MaterialTheme.colors.primary
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
    var isPaused by remember { mutableStateOf(true) }
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