package com.capputinodevelopment.rsvp.ui.Sreens

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.rsvp.ui.LockScreenOrientation
import kotlin.math.roundToInt
import kotlin.text.isEmpty
import kotlin.text.substring
import kotlin.text.take


@Composable
fun Read(
    modifier: Modifier,
    text: MutableState<String>,
    wpm: MutableFloatState,
    landscape: MutableState<Boolean>
) {
    if (landscape.value) LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    val digestedText by remember { mutableStateOf(text.value
        .replace("\n", " ")
        .replace("-", " ")
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    color = Color.Red,
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