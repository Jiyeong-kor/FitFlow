package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import android.content.Context
import android.speech.tts.TextToSpeech
import com.jeong.runninggoaltracker.feature.ai_coach.contract.SmartWorkoutSpeechContract
import java.util.Locale

class SmartWorkoutTextToSpeechController(context: Context) {
    private var isReady: Boolean = false
    private var pendingMessage: String? = null
    private val textToSpeech: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            textToSpeech.setLanguage(Locale.KOREAN)
            pendingMessage?.let { message ->
                pendingMessage = null
                speakInternal(message)
            }
        }
    }

    fun speak(message: String) {
        if (isReady) {
            speakInternal(message)
        } else {
            pendingMessage = message
        }
    }

    private fun speakInternal(message: String) {
        textToSpeech.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            SmartWorkoutSpeechContract.TEXT_TO_SPEECH_UTTERANCE_ID
        )
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
