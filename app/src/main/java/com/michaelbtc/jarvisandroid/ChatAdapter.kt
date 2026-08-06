package com.michaelbtc.jarvisandroid

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val textView: TextView)
        : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {

        val tv = TextView(parent.context)

        tv.textSize = 18f
        tv.setPadding(32,24,32,24)

        return ChatViewHolder(tv)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {

        val msg = messages[position]

        holder.textView.text = msg.text

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(16,16,16,16)

        if (msg.isUser) {
            holder.textView.setBackgroundColor(
                Color.parseColor("#2196F3")
            )
            holder.textView.setTextColor(Color.WHITE)
            params.gravity = Gravity.END
        } else {
            holder.textView.setBackgroundColor(
                Color.parseColor("#333333")
            )
            holder.textView.setTextColor(Color.WHITE)
            params.gravity = Gravity.START
        }

        holder.textView.layoutParams = params
    }

    override fun getItemCount() = messages.size
}
