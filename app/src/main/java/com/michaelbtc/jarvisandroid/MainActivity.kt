package com.michaelbtc.jarvisandroid

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var tts: TTSManager

    companion object {
        private const val REQUEST_RECORD_AUDIO = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TTSManager(this)

        resultText = findViewById(R.id.resultText)
        val talkButton = findViewById<Button>(R.id.talkButton)

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

                SpeechManager.start(this)

            }
        }
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

        if (requestCode == REQUEST_RECORD_AUDIO &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            SpeechManager.start(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SpeechManager.REQUEST_SPEECH &&
            resultCode == Activity.RESULT_OK
        ) {

            val results = data?.getStringArrayListExtra(
                android.speech.RecognizerIntent.EXTRA_RESULTS
            )

            val message = results?.get(0) ?: return

            resultText.text = "You: $message"

            JarvisApi.send(message) { reply ->

                runOnUiThread {

                    resultText.text =
                        "You: $message\n\nJarvis: $reply"

                    tts.speak(reply)
                }

            }

        }

    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
