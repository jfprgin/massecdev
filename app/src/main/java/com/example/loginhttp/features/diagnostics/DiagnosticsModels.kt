package com.example.loginhttp.features.diagnostics

import kotlinx.serialization.Serializable

data class TerminalMessageItem(
    val time: String,
    val message: String,
)

enum class CommandType {
    DEFAULT,
    CUSTOM,
}

@Serializable
data class CommandItem(
    val label: String,
    val requiresInput: Boolean,
    val commandPrefix: String,
    val type: CommandType = CommandType.CUSTOM,
)