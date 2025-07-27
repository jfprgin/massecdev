package com.example.loginhttp.features.diagnostics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class DiagnosticsViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<TerminalMessageItem>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()

    private val _customCommands = MutableStateFlow<List<CommandItem>>(emptyList())

    private val defaultCommands = DefaultCommands

    // Combine default and custom commands into a single flow
    val allCommands: StateFlow<List<CommandItem>> = _customCommands
        .map { custom -> defaultCommands + custom }
        .stateIn(viewModelScope, SharingStarted.Eagerly, defaultCommands)

    init {
        // Simulate a scale stream for demonstration purposes
        simulateScaleStream()
    }

    fun onInputChange(newInput: String) {
        _input.value = newInput
    }

    fun sendCommand(command: String) {
        if (command.isBlank()) return
        addMessage("Send: $command")    // UI update - runs on the main thread

        // TODO: send command to Bluetooth manager

        // Simulate a response for now
        viewModelScope.launch {
            val simulatedResponse = withContext(Dispatchers.IO) {
                delay(500)
                "Response for command: $command"
            }
            addMessage(simulatedResponse) // UI update - runs on the main thread
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
        if (item.type == CommandType.CUSTOM) {
            _customCommands.update { it + item }
        }
    }

    fun deleteCustomCommand(item: CommandItem) {
        if (item.type == CommandType.CUSTOM) {
            _customCommands.update { commandItems -> commandItems.filterNot { it == item } }
        }
    }

    fun editCustomCommand(original: CommandItem, updated: CommandItem) {
        if (original.type == CommandType.CUSTOM) {
            // Update the command in the list if it matches the original, the others remain unchanged
            _customCommands.update {
                it.map { cmd -> if (cmd == original) updated else cmd }
            }
        }
    }

    private fun addMessage(text: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(java.util.Date())
        val message = TerminalMessageItem(time, text)
        _messages.update { it + message }
    }

    fun loadCustomCommands(context: Context) {
        viewModelScope.launch {
            val loaded = CommandDataStore.loadCommands(context)
            _customCommands.value = loaded
        }
    }

    fun persistCustomCommands(context: Context) {
        viewModelScope.launch {
            CommandDataStore.saveCommands(context, _customCommands.value)
        }
    }

    private fun simulateScaleStream() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(2000) // Simulate periodic updates
                addMessage("Simulated scale reading: ${Math.random() * 100}")
            }
        }
    }
}