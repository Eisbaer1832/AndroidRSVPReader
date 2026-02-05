package com.capputinodevelopment.rsvp.ui.Sreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setup(modifier: Modifier = Modifier, textPassed: MutableState<String>, landscapePassed: MutableState<Boolean>, startRead: (text: String, wpm: Float, landscape: Boolean) -> Unit) {
    Column(modifier = modifier) {
        val text = rememberTextFieldState(textPassed.value)
        var landscape by remember { mutableStateOf(landscapePassed) }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = wpm.value.roundToInt().toString(), modifier = Modifier.weight(1f))
                Text("Landscape  ")
                Switch(
                    checked = landscape.value,
                    onCheckedChange = {
                        landscape.value = it
                    }
                )
            }
            Slider(state = wpm)
        }
        Button(
            onClick = {startRead(text.text.toString(), wpm.value,landscape.value)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Text("Los")
        }
    }
}
