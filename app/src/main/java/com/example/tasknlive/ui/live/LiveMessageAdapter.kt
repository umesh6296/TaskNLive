package com.example.tasknlive.ui.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasknlive.data.models.LiveMessage
import com.example.tasknlive.databinding.ItemLiveMessageBinding
import com.example.tasknlive.util.TimeUtils

class LiveMessageAdapter(
    private var messages: List<LiveMessage>,
    private val onDeleteClick: (LiveMessage) -> Unit
) : RecyclerView.Adapter<LiveMessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ItemLiveMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemLiveMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.apply {
            tvUserName.text = message.userName
            tvMessage.text = message.text
            tvTime.text = TimeUtils.formatTimestamp(message.timestamp)
            btnDeleteMessage.setOnClickListener { onDeleteClick(message) }
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<LiveMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}