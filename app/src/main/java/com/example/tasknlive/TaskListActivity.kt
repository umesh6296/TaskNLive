package com.example.tasknlive


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasknlive.data.models.Task
import com.example.tasknlive.data.repo.FirestoreTaskRepository
import com.example.tasknlive.databinding.ActivityTaskListBinding
import com.example.tasknlive.ui.tasks.TaskAdapter
import kotlinx.coroutines.flow.collectLatest


import kotlinx.coroutines.launch

class TaskListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var adapter: TaskAdapter
    private val repository = FirestoreTaskRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TaskAdapter(
            emptyList(),
            onItemClick = { task ->
                // Handle task click
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
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = adapter

        binding.btnAddTask.setOnClickListener {
            addSampleTask()
        }

        loadTasks()
        setupTaskListener()
    }

    private fun setupTaskListener() {
        lifecycleScope.launch {
            repository.getTasksFlow().collectLatest { tasks ->
                adapter.updateTasks(tasks)
            }
        }
    }

    private fun addSampleTask() {
        val newTask = Task(
            id = "",
            title = "Sample Task",
            description = "This is a sample description.",
            createdAt = System.currentTimeMillis(), // Add current timestamp
            isCompleted = false
        )

        lifecycleScope.launch {
            repository.addTask(newTask)
            loadTasks()
        }
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val tasks = repository.getTasks()
            adapter.updateTasks(tasks)
        }
    }
}
