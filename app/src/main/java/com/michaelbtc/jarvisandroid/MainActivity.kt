package com.michaelbtc.jarvisandroid

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var resultText: TextView
    private lateinit var statusText: TextView
    private lateinit var inputText: EditText
    private lateinit var tts: TextToSpeech

    companion object {
        private const val REQUEST_RECORD_AUDIO = 100
        private const val REQUEST_SPEECH = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        resultText = findViewById(R.id.resultText)
        statusText = findViewById(R.id.statusText)
        inputText = findViewById(R.id.inputText)

        val talkButton = findViewById<Button>(R.id.talkButton)
        val sendButton = findViewById<Button>(R.id.sendButton)

        talkButton.setOnClickListener {
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

        sendButton.setOnClickListener {
            val message = inputText.text.toString().trim()

            if (message.isNotEmpty()) {
                inputText.setText("")
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {

        resultText.append("\n\n👤 You:\n$message\n")

        statusText.text = "Thinking..."

        JarvisApi.send(message) { reply ->

            runOnUiThread {

                resultText.append("\n🤖 Jarvis:\n$reply\n")

                statusText.text = "Ready"

                tts.speak(
                    reply,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }
    }

    private fun startListening() {

        statusText.text = "Listening..."

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )

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

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == REQUEST_SPEECH &&
            resultCode == Activity.RESULT_OK
        ) {

            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val message = results?.get(0) ?: return

            sendMessage(message)
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            tts.language = Locale.US
        }
    }

    override fun onDestroy() {

        tts.stop()
        tts.shutdown()

        super.onDestroy()
    }
}
