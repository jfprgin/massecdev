package com.example.loginhttp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginhttp.ui.theme.DarkGray
import com.example.loginhttp.ui.theme.DeepNavy
import com.example.loginhttp.ui.theme.White
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.loginhttp.ui.theme.DarkText
import com.example.loginhttp.ui.theme.LightGray
import com.example.loginhttp.ui.theme.MassecRed

data class FormField(
    val label: String,
    val type: FieldType,
    val options: List<String> = emptyList(),
)

enum class FieldType {
    TEXT,
    DROPDOWN,
}

data class FormMode(
    val name: String,                 // Shown in dropdown
    val fields: List<FormField>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    title: String,
    fields: List<FormField>,
    onDismiss: () -> Unit,
    onSubmit: (inputs: List<String>) -> Unit,
    submitText: String = "Dodaj"
) {
    val inputValues = remember { mutableStateListOf(*Array(fields.size) { "" }) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)) {

            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
            Spacer(modifier = Modifier.height(20.dp))

            fields.forEachIndexed { index, field ->
                when (field.type) {
                    FieldType.TEXT -> {
                        OutlinedTextField(
                            value = inputValues[index],
                            onValueChange = { inputValues[index] = it },
                            label = { Text(field.label) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = DeepNavy,
                                unfocusedIndicatorColor = DarkGray
                            ),
                            singleLine = true
                        )
                    }

                    FieldType.DROPDOWN -> {
                        DropdownField(
                            label = field.label,
                            options = field.options,
                            selectedOption = inputValues[index],
                            onOptionSelected = { inputValues[index] = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSubmit(inputValues.toList())
                    onDismiss()
                },
                enabled = inputValues.none { it.isBlank() },
                colors = ButtonColors(
                    containerColor = DeepNavy,
                    contentColor = White,
                    disabledContainerColor = DarkGray,
                    disabledContentColor = White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(submitText, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetWithModes(
    title: String,
    modeSelectorLabel: String,
    modes: List<FormMode>,
    onDismiss: () -> Unit,
    onSubmit: (mode: FormMode, inputs: List<String>) -> Unit,
    submitText: String = "Dodaj",
) {

    var selectedIndex by remember { mutableStateOf(0) }
    val selectedMode = modes[selectedIndex]
    val activeFields = selectedMode.fields
    val inputs = remember(selectedIndex) { mutableStateListOf(*Array(activeFields.size) { "" }) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
              title,
                fontSize = 20.sp,
                color = DeepNavy,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            DropdownField(
                label = modeSelectorLabel,
                options = modes.map { it.name },
                selectedOption = selectedMode.name,
                onOptionSelected = { selected ->
                    selectedIndex = modes.indexOfFirst { it.name == selected }
                    inputs.clear()
                    inputs.addAll(List(modes[selectedIndex].fields.size) { "" })
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            activeFields.forEachIndexed { index, field ->
                when (field.type) {
                    FieldType.TEXT -> {
                        OutlinedTextField(
                            value = inputs[index],
                            onValueChange = { inputs[index] = it },
                            label = { Text(field.label) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = DeepNavy,
                                unfocusedIndicatorColor = DarkGray
                            ),
                            singleLine = true
                        )
                    }
                    FieldType.DROPDOWN -> {
                        DropdownField(
                            label = field.label,
                            options = field.options,
                            selectedOption = inputs[index],
                            onOptionSelected = { inputs[index] = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSubmit(selectedMode, inputs.toList())
                    onDismiss()
                },
                enabled = inputs.none { it.isBlank() },
                colors = ButtonColors(
                    containerColor = DeepNavy,
                    contentColor = White,
                    disabledContainerColor = DarkGray,
                    disabledContentColor = White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(submitText, fontSize = 16.sp, color = White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                textColor = DarkText,
                disabledTextColor = LightGray,
                backgroundColor = White,
                cursorColor = DeepNavy,
                focusedBorderColor = MassecRed,
                unfocusedBorderColor = DeepNavy,
                disabledBorderColor = LightGray,
                leadingIconColor = DeepNavy,
                disabledLeadingIconColor = LightGray,
                trailingIconColor = DeepNavy,
                focusedTrailingIconColor = MassecRed,
                disabledTrailingIconColor = LightGray,
                focusedLabelColor = MassecRed,
                unfocusedLabelColor = DeepNavy,
                disabledLabelColor = LightGray,
                placeholderColor = LightGray,
                disabledPlaceholderColor = LightGray,
            ),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = White,
            modifier = Modifier.border(1.dp, LightGray)
        ) {
            options.forEach { selection ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = selection,
                            color = DarkText,
                            fontSize = 14.sp,
                        )
                    },
                    onClick = {
                        onOptionSelected(selection)
                        expanded = false
                    }
                )
            }
        }
    }
}