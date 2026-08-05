package com.michaelbtc.jarvisandroid

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import java.util.Locale

object SpeechManager {

    const val REQUEST_SPEECH = 101

    fun start(activity: Activity) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        activity.startActivityForResult(
            intent,
            REQUEST_SPEECH
        )
    }
}
