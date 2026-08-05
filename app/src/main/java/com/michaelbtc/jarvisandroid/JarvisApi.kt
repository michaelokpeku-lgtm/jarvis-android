package com.michaelbtc.jarvisandroid

import okhttp3.*
import java.io.IOException

object JarvisApi {

    // Replace with your server later
    private const val URL = "http://10.0.2.2:5000/chat"

    private val client = OkHttpClient()

    fun send(message: String, callback: (String) -> Unit) {
        val body = RequestBody.create(
            "text/plain".toMediaType(),
            message
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
                callback(response.body?.string() ?: "No response")
            }
        })
    }
}
