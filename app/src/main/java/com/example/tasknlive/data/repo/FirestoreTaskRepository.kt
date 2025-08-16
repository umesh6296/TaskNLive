package com.example.tasknlive.data.repo

import android.util.Log
import com.example.tasknlive.data.models.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreTaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    suspend fun addTask(task: Task): String {
        val docRef = tasksCollection.add(task).await()
        return docRef.id
    }

    suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete().await()
    }

    fun getTasksFlow() = callbackFlow<List<Task>> {
        val listener = tasksCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach { doc ->
                    Log.d("FirestoreData", """
                    Document ID: ${doc.id}
                    Data: ${doc.data}
                    isCompleted Type: ${doc.get("isCompleted")?.javaClass?.simpleName}
                """.trimIndent())
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    val completed = doc.getBoolean("completed") ?: false
                    Log.d("FirestoreMapping", "Doc ${doc.id} - Raw completed: $completed")

                    Task(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        isCompleted = completed,
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getTasks(): List<Task> {
        val querySnapshot = tasksCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return querySnapshot.documents.map { doc ->
            Task(
                id = doc.id,
                title = doc.getString("title") ?: "",
                description = doc.getString("description") ?: "",
                createdAt = doc.getLong("createdAt") ?: 0L,
                isCompleted = doc.getBoolean("completed") ?: false
            )
        }
    }

    suspend fun updateTask(task: Task) {
        try {
            val updateData = mapOf(
                "title" to task.title,
                "description" to task.description,
                "completed" to task.isCompleted,
                "createdAt" to task.createdAt)
            tasksCollection.document(task.id).update(
                mapOf(
                    "completed" to task.isCompleted
                )
            ).await()
        } catch (e: Exception) {
            // Handle error (log it or show toast)
            throw e // Or handle it gracefully
        }
    }


}