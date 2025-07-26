package com.example.loginhttp.features.diagnostics

val DefaultCommands = listOf(
    CommandItem(label = "Zero", requiresInput = false, commandPrefix = "CMD-zero", type = CommandType.DEFAULT),
    CommandItem(label = "Reset", requiresInput = false, commandPrefix = "CMD-reset", type = CommandType.DEFAULT),
    CommandItem(label = "Get Configuration", requiresInput = false, commandPrefix = "CMD-getConfig", type = CommandType.DEFAULT),
    CommandItem(label = "Get Logs", requiresInput = false, commandPrefix = "CMD-getLogs", type = CommandType.DEFAULT),
    CommandItem(label = "Name", requiresInput = true, commandPrefix = "CMD-name", type = CommandType.DEFAULT),
    CommandItem(label = "Password", requiresInput = true, commandPrefix = "CMD-password", type = CommandType.DEFAULT),
    CommandItem(label = "Calibration", requiresInput = true, commandPrefix = "CMD-calib", type = CommandType.DEFAULT),
)