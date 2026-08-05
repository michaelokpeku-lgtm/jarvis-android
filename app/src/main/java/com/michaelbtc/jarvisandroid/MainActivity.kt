package com.michaelbtc.jarvisandroid

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var resultText: TextView
    private lateinit var tts: TextToSpeech

    private var continuousMode = false

    companion object {
        private const val REQUEST_RECORD_AUDIO = 100
        private const val REQUEST_SPEECH = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (continuousMode) {
                    runOnUiThread {
                        startListening()
                    }
                }
            }

            override fun onError(utteranceId: String?) {}
        })

        val talkButton = findViewById<Button>(R.id.talkButton)
        resultText = findViewById(R.id.resultText)

        talkButton.setOnClickListener {

            continuousMode = true

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO
                )

            } else {
                startListening()
            }
        }
    }

    private fun startListening() {

        resultText.text = "🎤 Listening..."

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        startActivityForResult(intent, REQUEST_SPEECH)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == REQUEST_RECORD_AUDIO &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {

            startListening()

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (
            requestCode == REQUEST_SPEECH &&
            resultCode == Activity.RESULT_OK
        ) {

            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val message = results?.get(0) ?: return

            resultText.text = "You: $message\n\nThinking..."

            JarvisApi.send(message) { reply ->

                runOnUiThread {

                    resultText.text =
                        "You: $message\n\nJarvis: $reply"

                    tts.speak(
                        reply,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "jarvis_reply"
                    )

                }

            }

        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }

    }

    override fun onDestroy() {

        continuousMode = false

        tts.stop()
        tts.shutdown()

        super.onDestroy()

    }
}
