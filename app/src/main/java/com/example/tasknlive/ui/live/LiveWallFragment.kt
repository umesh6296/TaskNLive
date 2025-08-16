package com.umesh.tasknlive.ui.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasknlive.data.models.LiveMessage

import com.example.tasknlive.data.repo.RealtimeLiveRepository
import com.example.tasknlive.databinding.FragmentLiveWallBinding
import com.example.tasknlive.ui.live.LiveMessageAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LiveWallFragment : Fragment() {

    private var _binding: FragmentLiveWallBinding? = null
    private val binding get() = _binding!!
    private val repository = RealtimeLiveRepository()
    private lateinit var adapter: LiveMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LiveMessageAdapter(
            emptyList(),
            onDeleteClick = { message ->
                lifecycleScope.launch {
                    repository.deleteMessage(message.id)
                }
            }
        )

        binding.recyclerViewLiveMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLiveMessages.adapter = adapter

        binding.btnSend.setOnClickListener {
            val name = binding.etName.text.toString()
            val message = binding.etMessage.text.toString()
            if (name.isNotBlank() && message.isNotBlank()) {
                repository.sendMessage(
                    LiveMessage(userName = name, text = message)

                )
                binding.etMessage.text.clear()
            }
        }

        lifecycleScope.launch {
            repository.getMessagesFlow().collectLatest { messages ->
                adapter.updateMessages(messages)
                // Add safety check before scrolling
                if (messages.isNotEmpty()) {
                    binding.recyclerViewLiveMessages.post {
                        binding.recyclerViewLiveMessages.smoothScrollToPosition(messages.size - 1)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
