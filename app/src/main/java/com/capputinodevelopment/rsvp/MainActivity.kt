@file:OptIn(ExperimentalMaterial3Api::class)

package com.capputinodevelopment.rsvp

import android.content.Intent
import android.os.Bundle
import androidx.activity.BackEventCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.capputinodevelopment.rsvp.ui.Sreens.Read
import com.capputinodevelopment.rsvp.ui.Sreens.Setup
import com.capputinodevelopment.rsvp.ui.theme.RSVPTheme
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

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
            val started = rememberSaveable{ mutableStateOf(false) }
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
            val text = rememberSaveable{ mutableStateOf("") }
            val wpm = rememberSaveable { mutableFloatStateOf(1f) }
            val landscape = rememberSaveable { mutableStateOf(true) }
            when {
                intent.action == Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        text.value = handleSendText(intent)

                    }
                }
            }
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
                            landscapePassed = landscape,
                            startRead = { textPassed, wpmPassed,landscapePassed ->
                                text.value = textPassed
                                wpm.floatValue = wpmPassed
                                started.value = true
                                landscape.value = landscapePassed
                            }
                        )
                    }else {
                        Read(
                            modifier = Modifier.padding(innerPadding),
                            text = text,
                            wpm = wpm,
                            landscape = landscape
                        )
                    }
                }
            }
        }
    }
}
