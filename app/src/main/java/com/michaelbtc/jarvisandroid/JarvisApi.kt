package com.michaelbtc.jarvisandroid

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object JarvisApi {

    // Replace with your server later
    private const val URL = "http://127.0.0.1:5000/chat"

    private val client = OkHttpClient()

    fun send(message: String, callback: (String) -> Unit) {
        val json = """{"message":"$message"}"""

        val body = json.toRequestBody(
            "application/json".toMediaType()
        )

        val request = Request.Builder()
            .url(URL)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Connection failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string() ?: ""

                try {
                    val reply = org.json.JSONObject(json)
                        .getString("reply")

                    callback(reply)
                } catch (e: Exception) {
                    callback(json)
                }
            }
        })
    }
}

