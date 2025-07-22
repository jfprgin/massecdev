package com.example.loginhttp.features.diagnostics

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed
import com.example.loginhttp.ui.theme.White


@Composable
fun DiagnosticsScreen(viewModel: DiagnosticsViewModel) {
    val messages by viewModel.messages.collectAsState()
    val input by viewModel.input.collectAsState()
    val commands by viewModel.commands.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(16.dp)
    ) {

        // Terminal Log Display
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            LazyColumn(state = listState) {
                items(messages) { msg ->
                    Text(
                        text = "${msg.time} - ${msg.message}",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input field + Send button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter command...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedIndicatorColor = White,
                    unfocusedIndicatorColor = White,
                ),
                // Rounded corners
                shape = MaterialTheme.shapes.small,

            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.sendCommand(input) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MassecRed,
                    contentColor = White
                ),
            ) {
                Text("Send")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CommandSection(
            commands = commands,
            onCommandSend = { cmd, input ->
                viewModel.sendCommandFromItem(cmd, input)
            },
            onAddCustom = { commandItem ->
                viewModel.addCustomCommand(commandItem)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CommandSection(
    commands: List<CommandItem>,
    onCommandSend: (CommandItem, String?) -> Unit,
    onAddCustom: (CommandItem) -> Unit
) {
    var inputDialogCommand by remember { mutableStateOf<CommandItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    CommandButtonRow(
        commands = commands,
        onButtonClick = { cmd ->
            if (cmd.requiresInput) {
                inputDialogCommand = cmd
            } else {
                onCommandSend(cmd, null)
            }
        },
        onAddClick = { showAddDialog = true }
    )

    inputDialogCommand?.let { cmd ->
        InputDialog(
            command = cmd,
            onDismiss = { inputDialogCommand = null },
            onConfirm = { inputValue ->
                onCommandSend(cmd, inputValue)
                inputDialogCommand = null
            }
        )
    }

    if (showAddDialog) {
        AddCommandDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { item: CommandItem ->
                onAddCustom(item)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CommandButtonRow(
    commands: List<CommandItem>,
    onButtonClick: (CommandItem) -> Unit,
    onAddClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var viewportWidthPx by remember { mutableIntStateOf(1) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { viewportWidthPx = it.size.width }
                .horizontalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                val splitIndex = (commands.size + 1) / 2
                val (firstRow, secondRow) = commands.chunked(splitIndex)

                listOf(firstRow, secondRow).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { cmd ->
                            OutlinedButton(
                                onClick = { onButtonClick(cmd) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = White,
                                    contentColor = DeepNavy
                                ),
                                border = BorderStroke(1.dp, DeepNavy)
                            ) {
                                Text(cmd.label)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        ScrollbarKnob(scrollState = scrollState, viewportWidth = viewportWidthPx)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onAddClick,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = White,
                contentColor = MassecRed
            ),
            border = BorderStroke(1.dp, MassecRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Add Command")
        }
    }
}

@Composable
fun ScrollbarKnob(
    scrollState: ScrollState,
    viewportWidth: Int,
    trackHeight: Dp = 4.dp,
    knobWidthDp: Dp = 80.dp
) {
    val proportionScrolled = if (scrollState.maxValue > 0)
        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    else 0f

    val maxKnobOffset = with(LocalDensity.current) { (viewportWidth - knobWidthDp.toPx()).coerceAtLeast(0f) }

    val knobOffset = maxKnobOffset * proportionScrolled

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(trackHeight)
            .background(DarkGray.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .offset(x = with(LocalDensity.current) { knobOffset.toDp() })
                .width(knobWidthDp)
                .fillMaxHeight()
                .background(DeepNavy, shape = MaterialTheme.shapes.small)
        )
    }
}

@Composable
fun InputDialog(
    command: CommandItem,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter ${command.label}") },
        text = {
            TextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                placeholder = { Text("Value...") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(inputValue) }) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddCommandDialog(
    onDismiss: () -> Unit,
    onConfirm: (CommandItem) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var commandPrefix by remember { mutableStateOf("") }
    var requiresInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Custom Command") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Button Label") }
                )
                TextField(
                    value = commandPrefix,
                    onValueChange = { commandPrefix = it },
                    label = { Text("Command Value") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = requiresInput,
                        onCheckedChange = { requiresInput = it }
                    )
                    Text("Requires input")
                    }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (label.isNotBlank() && commandPrefix.isNotBlank()) {
                    val item = CommandItem(
                        label = label,
                        requiresInput = requiresInput,
                        commandPrefix = commandPrefix
                    )
                    onConfirm(item)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DiagnosticsScreenPreview() {
    val viewModel = DiagnosticsViewModel()
    DiagnosticsScreen(viewModel = viewModel)
}