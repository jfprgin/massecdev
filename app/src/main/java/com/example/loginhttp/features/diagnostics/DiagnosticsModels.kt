package com.example.loginhttp.features.diagnostics

data class TerminalMessageItem(
    val time: String,
    val message: String,
)

data class CommandItem(
    val label: String,
    val requiresInput: Boolean,
    val commandPrefix: String = "CMD-"
)