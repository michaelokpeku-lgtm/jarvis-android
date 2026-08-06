package com.michaelbtc.jarvisandroid

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val time: Long = System.currentTimeMillis()
)
