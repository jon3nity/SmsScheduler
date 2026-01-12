package com.example.smsschedulerapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDatePicker(
    label: String,
    initialDate: Long? = null,
    onDateSelected: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDate?.let { dateFormatter.format(Date(it)) } ?: "",
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Button(
                    onClick = { isDatePickerVisible = true }
                ) {
                    Text("Select")
                }
            }
        )

        if (isDatePickerVisible) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate
            )

            DatePickerDialog(
                onDismissRequest = { isDatePickerVisible = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = it
                                onDateSelected(it)
                            }
                            isDatePickerVisible = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isDatePickerVisible = false }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }
}

// Example usage in a form
@Composable
fun ScheduleForm() {
    var scheduledDate by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Schedule SMS")

        Spacer(modifier = Modifier.height(16.dp))

        CalendarDatePicker(
            label = "Date",
            initialDate = scheduledDate,
            onDateSelected = { date ->
                scheduledDate = date
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add time picker, message field, recipient field, etc.

        Button(
            onClick = { /* Handle scheduling */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule SMS")
        }
    }
}