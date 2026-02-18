package com.example.bluetooth_chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class TaskProcessor {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val taskChannel = Channel<suspend () -> Unit>(Channel.Factory.UNLIMITED)

    init {
        scope.launch {
            for (task in taskChannel) {
                try {
                    task()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun submit(task: suspend () -> Unit) {
        taskChannel.send(task)
    }

    fun stop() {
        scope.cancel()
    }
}