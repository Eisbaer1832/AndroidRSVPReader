@file:OptIn(ExperimentalMaterial3Api::class)

package com.capputinodevelopment.rsvp

import android.content.Intent
import android.os.Bundle
import androidx.activity.BackEventCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.rsvp.ui.theme.RSVPTheme
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt

private fun handleSendText(intent: Intent): String {
    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
        return it
    }
    return ""
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val started = remember { mutableStateOf(false) }
            PredictiveBackHandler { backEvent: Flow<BackEventCompat> ->
                try {
                    backEvent.collect { event ->
                    }
                } catch (e: CancellationException) {
                    started.value = true
                } finally {
                    started.value = false
                }
            }
            val text = remember { mutableStateOf("") }
            when {
                intent.action == Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        text.value = handleSendText(intent)

                    }
                }
            }
            val wpm = remember { mutableFloatStateOf(1f) }
            RSVPTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (started.value) {
                            TopAppBar(
                                navigationIcon = {
                                    IconButton({
                                        started.value = false
                                    }) {
                                        Icon(Icons.AutoMirrored.Default.ArrowBack,"back")
                                    }
                                },
                                title = { }
                            )
                        }
                    }
                ) { innerPadding ->
                    if (!started.value) {
                        Setup(
                            modifier = Modifier.padding(innerPadding),
                            textPassed = text,
                            startRead = { textPassed, wpmPassed ->
                                text.value = textPassed
                                wpm.floatValue = wpmPassed
                                started.value = true
                            }
                        )
                    }else {
                        Read(
                            modifier = Modifier.padding(innerPadding),
                            text = text,
                            wpm = wpm
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Setup(modifier: Modifier = Modifier, textPassed: MutableState<String>, startRead: (text: String, wpm: Float) -> Unit) {
    Column(modifier = modifier) {
        val text = rememberTextFieldState(textPassed.value)
        val wpm = rememberSliderState(
            valueRange = 100f .. 1000f,
            value = 300f
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
            state = text,
            label = { Text("Text") },
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = wpm.value.roundToInt().toString())
            Slider(state = wpm)
        }
        Button(
            onClick = {startRead(text.text.toString(), wpm.value)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Text("Los")
        }
    }
}

@Composable
fun Read(modifier: Modifier, text: MutableState<String>, wpm: MutableFloatState) {
    val digestedText by remember { mutableStateOf(text.value
        .replace("\n", " ")
        .replace(".", ". ") // hacky way for adding a pause after a full stop
        .split(" "))
    }
    var currentIndex by remember { mutableIntStateOf(0) }
    val msPerWord by remember { mutableIntStateOf(((60000 / wpm.floatValue)).roundToInt()) }


    LaunchedEffect(digestedText, wpm.value) {
        val startTime = System.currentTimeMillis()
        while (currentIndex < digestedText.lastIndex) {
            val elapsed = System.currentTimeMillis() - startTime
            currentIndex = (elapsed / msPerWord).toInt().coerceAtMost(digestedText.lastIndex)
            withFrameMillis { }
        }
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val word = digestedText[currentIndex]
        val opv = getOPV(word)
        if (!word.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    textAlign = TextAlign.End,
                    fontSize = 50.sp,
                    text = word.take(opv),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    textAlign = TextAlign.Center,
                    fontSize = 50.sp,
                    color = MaterialTheme.colorScheme.error,
                    text = word[opv].toString()
                )
                Text(
                    textAlign = TextAlign.Start,
                    fontSize = 50.sp,
                    text = word.substring(opv +1, word.length),
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Clip
                )
            }

        }
    }
}

fun getOPV(word: String): Int {
    // based on this https://codepen.io/keithwyland/pen/yLyLNz
    return (((word.length + 1) * 0.4).roundToInt()) - 1
}