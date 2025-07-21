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

        val commands = listOf("zero", "calibration", "name", "password", "reset", "get configuration", "get logs")
        TwoLineCommandRowWithScrollbar(commands, viewModel::sendPredefinedCommand)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TwoLineCommandRowWithScrollbar(
    commands: List<String>,
    onCommandClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var viewportWidthPx by remember { mutableStateOf(1) }

    Column {
        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    viewportWidthPx = it.size.width
                }
                .horizontalScroll(scrollState)
                .fillMaxWidth()
        ) {
            // Wrap commands into two horizontal lines
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                val splitIndex = (commands.size + 1) / 2
                val firstRow = commands.subList(0, splitIndex)
                val secondRow = commands.subList(splitIndex, commands.size)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    firstRow.forEach { cmd ->
                        OutlinedButton(
                            onClick = { onCommandClick(cmd) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = White,
                                contentColor = DeepNavy
                            ),
                            border = BorderStroke(1.dp, DeepNavy)
                        ) {
                            Text(cmd)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    secondRow.forEach { cmd ->
                        OutlinedButton(
                            onClick = { onCommandClick(cmd) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = White,
                                contentColor = DeepNavy
                            ),
                            border = BorderStroke(1.dp, DeepNavy)
                        ) {
                            Text(cmd)
                        }
                    }
                }
            }
        }

        ScrollbarKnob(
            scrollState = scrollState,
            viewportWidth = viewportWidthPx
        )
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DiagnosticsScreenPreview() {
    val viewModel = DiagnosticsViewModel()
    DiagnosticsScreen(viewModel = viewModel)
}