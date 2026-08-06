package com.michaelbtc.jarvisandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        private const val USER = 0
        private const val JARVIS = 1
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.messageText)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) USER else JARVIS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        val layout = if (viewType == USER)
            R.layout.chat_item_user
        else
            R.layout.chat_item_jarvis

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.text.text = messages[position].message
    }

    override fun getItemCount(): Int = messages.size

    fun add(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
