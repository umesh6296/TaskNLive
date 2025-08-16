package com.example.tasknlive.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasknlive.data.repo.FirestoreTaskRepository
import com.example.tasknlive.databinding.FragmentTasksBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskAdapter
    private val repository = FirestoreTaskRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TaskAdapter(
            emptyList(),
            onItemClick = { task ->

            },

            onDeleteClick = { task ->
                lifecycleScope.launch {
                    repository.deleteTask(task.id)
                }
            },
            onCompleteClick = { task ->
                lifecycleScope.launch {
                    repository.updateTask(task.copy(isCompleted = !task.isCompleted))
                }
            }
        )

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter

        // Set up real-time listener
        setupTaskListener()
    }

    private fun setupTaskListener() {
        lifecycleScope.launch {
            repository.getTasksFlow().collectLatest { tasks ->
                adapter.updateTasks(tasks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}