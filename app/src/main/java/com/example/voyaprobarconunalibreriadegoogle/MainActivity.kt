package com.example.voyaprobarconunalibreriadegoogle

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.BorderStroke
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyaprobarconunalibreriadegoogle.ui.theme.VoyAProbarConUnaLibreriaDeGoogleTheme
import java.util.Locale
object SharedState{
    var currentIndex = mutableStateOf(0)
    var isSwipeProcessed = mutableStateOf(false)
    lateinit var tts:TextToSpeech
}
class MainActivity : ComponentActivity() {
    //private lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize TextToSpeech
        SharedState.tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                SharedState.tts.language = Locale.US
            }
        }
        setContent {
            TalkBackScreen { message ->
                SharedState.tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
}

@Composable
fun TalkBackScreen(onSpeak: (String) -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val languages = listOf("Español", "English", "Français")
    val languageLocales = listOf(Locale("es", "ES"), Locale.UK, Locale.FRENCH)

    // Cambia el idioma del TTS según el índice actual
    fun updateTTSLanguage(index: Int) {
        val locale = languageLocales[index]
        val result = SharedState.tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "Language not supported: $locale")
        }
    }
    // Llama inicialmente al idioma correspondiente


    // Recuerda el detector de gestos
    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = {
                val buttonName = "Button ${currentIndex + 1}"
                Log.d("Gesture", "Double Tap on $buttonName")
                onSpeak("Clicking on $buttonName")
            }
        )
    }.pointerInput(Unit) {

        detectHorizontalDragGestures(
            onDragStart = {
            SharedState.isSwipeProcessed.value = false
        },onHorizontalDrag = { _, dragAmount ->
if(!SharedState.isSwipeProcessed.value){
            if (dragAmount > 0f) {
                currentIndex = (currentIndex + 1).mod(3)
                updateTTSLanguage(currentIndex)
                SharedState.isSwipeProcessed.value = true
                onSpeak("Selected Button ${languages[currentIndex]}")
            } else {
                currentIndex = (currentIndex - 1).mod(3)
                updateTTSLanguage(currentIndex)
                SharedState.isSwipeProcessed.value = true
                onSpeak("Selected Button ${languages[currentIndex]}")
            }

        }},
            onDragEnd = {
                SharedState.isSwipeProcessed.value = false
            }
            )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .then(gestureModifier)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            for (i in 0..2) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .padding(8.dp)
                        .width(200.dp)
                        .height(60.dp),
                    border = if (i == currentIndex) {
                        BorderStroke(2.dp, Color.Blue)
                    } else null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = languages[i], fontSize = 18.sp)
                }
            }
        }
    }
}