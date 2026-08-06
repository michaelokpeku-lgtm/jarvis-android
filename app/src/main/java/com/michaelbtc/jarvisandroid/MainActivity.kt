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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var statusText: TextView
    private lateinit var inputText: EditText
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.chatList)
        statusText = findViewById(R.id.statusText)
        inputText = findViewById(R.id.messageInput)

        adapter = ChatAdapter(messages)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        tts = TextToSpeech(this, this)

        addJarvisMessage("Welcome back, Michael.\nI'm ready whenever you are.")

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

        findViewById<Button>(R.id.sendButton).setOnClickListener {

            val message = inputText.text.toString().trim()

            if (message.isNotEmpty()) {

                inputText.setText("")

                sendMessage(message)

            }

        }

    }

    private fun startListening() {

        statusText.text = "● Listening..."

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

    private fun sendMessage(message: String) {

        addUserMessage(message)

        statusText.text = "● Thinking..."

        JarvisApi.send(message) { reply ->

            runOnUiThread {

                addJarvisMessage(reply)

                statusText.text = "● Speaking..."

                tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "jarvis")

                statusText.text = "● Ready"

            }

        }

    }

    private fun addUserMessage(text: String) {

        messages.add(ChatMessage(text, true))

        adapter.notifyItemInserted(messages.size - 1)

        recyclerView.scrollToPosition(messages.size - 1)

    }

    private fun addJarvisMessage(text: String) {

        messages.add(ChatMessage(text, false))

        adapter.notifyItemInserted(messages.size - 1)

        recyclerView.scrollToPosition(messages.size - 1)

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

        if (requestCode == REQUEST_SPEECH &&
            resultCode == Activity.RESULT_OK
        ) {

            val results = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val message = results?.firstOrNull() ?: return

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
