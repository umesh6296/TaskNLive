package com.example.tasknlive

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tasknlive.data.models.Task
import com.example.tasknlive.data.repo.FirestoreTaskRepository
import com.example.tasknlive.databinding.ActivityAddTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private val repository = FirestoreTaskRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()

            if (title.isBlank()) {
                binding.etTitle.error = "Title cannot be empty"
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                description = description,
                createdAt = System.currentTimeMillis(),
                isCompleted = false
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // First generate a temporary ID
                    val tempTask = task.copy(id = UUID.randomUUID().toString())
                    // Then get the real ID from Firestore
                    val taskId = repository.addTask(tempTask)
                    val finalTask = tempTask.copy(id = taskId)

                    runOnUiThread {
                        Toast.makeText(
                            this@AddTaskActivity,
                            "Task added with ID: $taskId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}

