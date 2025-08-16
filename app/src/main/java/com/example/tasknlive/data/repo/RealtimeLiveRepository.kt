package com.example.tasknlive.data.repo

import com.example.tasknlive.data.models.LiveMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class RealtimeLiveRepository {
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("live_messages")

    fun sendMessage(message: LiveMessage) {
        // Generate new reference and get its key
        val newMessageRef = messagesRef.push()
        // Create message with the generated ID
        val messageWithId = message.copy(id = newMessageRef.key ?: "")
        // Set value with the correct ID
        newMessageRef.setValue(messageWithId)
    }
    fun getMessagesFlow() = callbackFlow<List<LiveMessage>> {
        val listener = messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.map { child ->
                    child.getValue(LiveMessage::class.java)?.copy(id = child.key ?: "")
                }.filterNotNull()
                trySend(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { messagesRef.removeEventListener(listener) }
    }

    fun deleteMessage(messageId: String) {
        database.getReference("live_messages").child(messageId).removeValue()
    }
}