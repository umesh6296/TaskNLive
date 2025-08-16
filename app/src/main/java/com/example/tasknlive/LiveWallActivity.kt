package com.example.tasknlive

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasknlive.data.models.LiveMessage
import com.example.tasknlive.data.repo.RealtimeLiveRepository
import com.example.tasknlive.databinding.ActivityLiveWallBinding
import com.example.tasknlive.ui.live.LiveMessageAdapter  // Ensure this import

import com.example.tasknlive.util.UserSession
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LiveWallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiveWallBinding
    private lateinit var adapter: LiveMessageAdapter
    private val repository = RealtimeLiveRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveWallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etName.setText(UserSession.getUsername())

        adapter = LiveMessageAdapter(
            emptyList(),
            onDeleteClick = { message ->
                lifecycleScope.launch {
                    repository.deleteMessage(message.id)
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = adapter

        binding.btnSend.setOnClickListener {
            val name = binding.etName.text.toString().trim().takeIf { it.isNotEmpty() } ?: "Guest"
            val message = binding.etMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                repository.sendMessage(
                    LiveMessage(
                        userName = name,
                        text = message,
                        timestamp = System.currentTimeMillis()
                    )
                )
                binding.etMessage.text.clear()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            repository.getMessagesFlow().collectLatest { messages ->
                adapter.updateMessages(messages)
                if (messages.isNotEmpty() && binding.recyclerView.layoutManager != null) {
                    binding.recyclerView.post {
                        try {
                            binding.recyclerView.smoothScrollToPosition(messages.size - 1)
                        } catch (e: IllegalArgumentException) {
                            binding.recyclerView.scrollToPosition(messages.size - 1)
                        }
                    }
                }
            }
        }
    }
}