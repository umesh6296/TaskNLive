package com.example.tasknlive.ui.tasks

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.tasknlive.databinding.DialogAddTaskBinding

class AddTaskDialogFragment(
    private val onTaskAdded: (String, String) -> Unit  // title & description callback
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val title = binding.etTaskTitle.text.toString().trim()
                val description = binding.etTaskDescription.text.toString().trim()

                if (title.isNotBlank()) {
                    onTaskAdded(title, description)
                    Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}


