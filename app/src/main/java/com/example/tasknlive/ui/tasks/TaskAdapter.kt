package com.example.tasknlive.ui.tasks

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tasknlive.R
import com.example.tasknlive.data.models.Task
import com.example.tasknlive.databinding.ItemTaskBinding
import com.example.tasknlive.util.TimeUtils

class TaskAdapter(
    private var tasks: List<Task>,
    private val onItemClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onCompleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        Log.d("TaskDebug", "Task: ${task.title}, DB Completed: ${task.isCompleted}")
        holder.binding.apply {
            // Set task data
            tvTitle.text = task.title ?: ""
            tvDescription.text = task.description ?: ""
            tvDate.text = TimeUtils.formatTimestamp(task.createdAt)
            Log.d("TaskDebug", "Task ID: ${task.id}, Completed: ${task.isCompleted}")
            // Handle Done Button vs Checkmark
            if (task.isCompleted) {

                // Task is COMPLETED -> Show Tick, Hide Done Button
                btnDone.visibility = View.GONE
                ivDoneCheck.visibility = View.VISIBLE
            } else {
                // Task is NOT COMPLETED -> Show Done Button, Hide Tick
                btnDone.visibility = View.VISIBLE
                ivDoneCheck.visibility = View.GONE
            }

            // Strikethrough text if completed
            tvTitle.paintFlags = if (task.isCompleted) {
                tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Click Listeners
            btnDelete.setOnClickListener { onDeleteClick(task) }
            btnDone.setOnClickListener {
                btnDone.visibility = View.GONE
                ivDoneCheck.visibility = View.VISIBLE
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


                onCompleteClick(task)
            }
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = tasks.size
            override fun getNewListSize() = newTasks.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                tasks[oldPos].id == newTasks[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                tasks[oldPos] == newTasks[newPos]
        })
        tasks = newTasks
        diff.dispatchUpdatesTo(this)
    }

    companion object {
        private val PAYLOAD_COMPLETED = Any()
    }
}