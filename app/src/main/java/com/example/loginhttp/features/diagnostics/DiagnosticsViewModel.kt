package com.example.loginhttp.features.diagnostics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DiagnosticsViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<TerminalMessageItem>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    init {
        // Simulate a scale stream for demonstration purposes
        simulateScaleStream()
    }

    fun onInputChange(newInput: String) {
        _input.value = newInput
    }

    fun sendCommand(command: String) {
        if (command.isBlank()) return
        addMessage("Send: $command")

        // TODO: send command to Bluetooth manager

        // Simulate a response for now
        CoroutineScope(Dispatchers.IO).launch {
            kotlinx.coroutines.delay(500)
            addMessage("Response: Simulated response for '$command'")
        }

        _input.value = "" // Clear input after sending
    }

    fun sendPredefinedCommand(cmd: String) = sendCommand(cmd)

    private fun addMessage(text: String) {
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val message = TerminalMessageItem(time, text)
        _messages.update { it + message }
    }

    private fun simulateScaleStream() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                kotlinx.coroutines.delay(2000) // Simulate periodic updates
                addMessage("Simulated scale reading: ${Math.random() * 100}")
            }
        }
    }
}