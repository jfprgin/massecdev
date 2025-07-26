package com.example.loginhttp.features.diagnostics

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/* =================================================================================================
* CommandDataStore is a singleton object that provides methods to save and load command data
================================================================================================= */

val Context.commandDataStore by preferencesDataStore(name = "diagnostics_commands")

object CommandStoreKeys {
    val COMMANDS = stringPreferencesKey("commands_json")
}

object CommandDataStore {
    private val json = Json { encodeDefaults = true; prettyPrint = false }

    suspend fun saveCommands(context: Context, commands: List<CommandItem>) {
        // Convert the list of CommandItem to JSON string
        context.commandDataStore.edit { prefs ->
            prefs[CommandStoreKeys.COMMANDS] = json.encodeToString(commands)
        }
    }

    suspend fun loadCommands(context: Context): List<CommandItem> {
        // Load the commands from the DataStore
        val prefs = context.commandDataStore.data.first()
        val raw = prefs[CommandStoreKeys.COMMANDS] ?: return emptyList()
        return try {
            json.decodeFromString(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }
}