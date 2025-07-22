package com.example.loginhttp.features.diagnostics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiagnosticsViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<TerminalMessageItem>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    private val _commands = MutableStateFlow(
        listOf(
            CommandItem(label = "Zero", requiresInput = false, commandPrefix = "CMD-zero"),
            CommandItem(label = "Reset", requiresInput = false, commandPrefix = "CMD-reset"),
            CommandItem(label = "Get Configuration", requiresInput = false, commandPrefix = "CMD-getConfig"),
            CommandItem(label = "Get Logs", requiresInput = false, commandPrefix = "CMD-getLogs"),
            CommandItem(label = "Name", requiresInput = true, commandPrefix = "CMD-name"),
            CommandItem(label = "Password", requiresInput = true, commandPrefix = "CMD-password"),
            CommandItem(label = "Calibration", requiresInput = true, commandPrefix = "CMD-calib")
        )
    )
    val commands = _commands.asStateFlow()

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

    fun sendCommandFromItem(cmd: CommandItem, userInput: String? = null) {
        val fullCommand = if (cmd.requiresInput && userInput != null) {
            "${cmd.commandPrefix}${userInput}"
        } else {
            cmd.commandPrefix
        }

        sendCommand(fullCommand)
    }

    fun addCustomCommand(item: CommandItem) {
        _commands.update { it + item }
    }

    private fun addMessage(text: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
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