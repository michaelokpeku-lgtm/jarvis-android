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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        private const val REQUEST_RECORD_AUDIO = 100
        private const val REQUEST_SPEECH = 101
    }

    private lateinit var adapter: ChatAdapter
    private lateinit var chatList: RecyclerView
    private lateinit var input: EditText
    private lateinit var statusText: TextView
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        statusText = findViewById(R.id.statusText)
        input = findViewById(R.id.messageInput)
        chatList = findViewById(R.id.chatList)

        adapter = ChatAdapter(mutableListOf())
        chatList.layoutManager = LinearLayoutManager(this)
        chatList.adapter = adapter

        findViewById<Button>(R.id.sendButton).setOnClickListener {

            val text = input.text.toString().trim()

            if (text.isEmpty()) return@setOnClickListener

            sendMessage(text)

            input.setText("")
        }

        findViewById<Button>(R.id.talkButton).setOnClickListener {

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

    private fun sendMessage(message: String) {

        adapter.add(ChatMessage(message, true))
        chatList.scrollToPosition(adapter.itemCount - 1)

        statusText.text = "🤖 Jarvis is thinking..."

        JarvisApi.send(message) { reply ->

            runOnUiThread {

                statusText.text = "● Ready"

                adapter.add(ChatMessage(reply, false))

                chatList.scrollToPosition(adapter.itemCount - 1)

                tts.speak(
                    reply,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "jarvis"
                )

            }

        }

    }

    private fun startListening() {

        statusText.text = "🎤 Listening..."

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

        startActivityForResult(
            intent,
            REQUEST_SPEECH
        )

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

            val text = data
                ?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                ?.firstOrNull()

            if (text != null) {

                sendMessage(text)

            }

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
