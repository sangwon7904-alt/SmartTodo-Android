package com.example.smarttodo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        priority: Int,
        dueDateMillis: Long?,
        dueHour: Int?,
        dueMinute: Int?
    ) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableIntStateOf(2) }
    var selectedDueDateMillis by remember {
        mutableStateOf<Long?>(null)
    }
    var selectedDueHour by remember {
        mutableStateOf<Int?>(null)
    }
    var selectedDueMinute by remember {
        mutableStateOf<Int?>(null)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val selectedDueDateText =
        selectedDueDateMillis?.let { millis ->
            SimpleDateFormat(
                "yyyy년 M월 d일",
                Locale.KOREAN
            ).format(Date(millis))
        } ?: "마감일 없음"

    val selectedDueTimeText =
        if (selectedDueHour != null && selectedDueMinute != null) {
            String.format(
                Locale.KOREAN,
                "%02d:%02d",
                selectedDueHour,
                selectedDueMinute
            )
        } else {
            "마감 시간 없음"
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("새 할 일 추가")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = todoText,
                    onValueChange = {
                        todoText = it
                    },
                    label = {
                        Text("할 일을 입력하세요")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("우선순위")

                PriorityOption(
                    text = "높음",
                    selected = selectedPriority == 3,
                    onClick = {
                        selectedPriority = 3
                    }
                )

                PriorityOption(
                    text = "보통",
                    selected = selectedPriority == 2,
                    onClick = {
                        selectedPriority = 2
                    }
                )

                PriorityOption(
                    text = "낮음",
                    selected = selectedPriority == 1,
                    onClick = {
                        selectedPriority = 1
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "마감일",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedDueDateText)
                }

                if (selectedDueDateMillis != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showTimePicker = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDueTimeText)
                    }

                    if (
                        selectedDueHour != null &&
                        selectedDueMinute != null
                    ) {
                        TextButton(
                            onClick = {
                                selectedDueHour = null
                                selectedDueMinute = null
                            }
                        ) {
                            Text("마감 시간 삭제")
                        }
                    }

                    TextButton(
                        onClick = {
                            selectedDueDateMillis = null
                            selectedDueHour = null
                            selectedDueMinute = null
                        }
                    ) {
                        Text("마감일 삭제")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (todoText.isNotBlank()) {
                        onConfirm(
                            todoText,
                            selectedPriority,
                            selectedDueDateMillis,
                            selectedDueHour,
                            selectedDueMinute
                        )
                    }
                }
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("취소")
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDueDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDueDateMillis =
                            datePickerState.selectedDateMillis

                        showDatePicker = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showTimePicker) {
        val currentTime = Calendar.getInstance()

        val timePickerState = rememberTimePickerState(
            initialHour = selectedDueHour
                ?: currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = selectedDueMinute
                ?: currentTime.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
            },
            title = {
                Text("마감 시간 선택")
            },
            text = {
                TimePicker(
                    state = timePickerState
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDueHour = timePickerState.hour
                        selectedDueMinute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
private fun PriorityOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(text = text)
    }
}