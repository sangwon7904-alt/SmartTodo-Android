package com.kimsangwon.smarttodo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.kimsangwon.smarttodo.data.model.Todo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        priority: Int,
        dueDateMillis: Long?,
        dueHour: Int?,
        dueMinute: Int?
    ) -> Unit
) {
    var editText by remember(todo.id) {
        mutableStateOf(todo.title)
    }

    var editPriority by remember(todo.id) {
        mutableIntStateOf(todo.priority)
    }

    var editDueDateMillis by remember(todo.id) {
        mutableStateOf(todo.dueDateMillis)
    }

    var editDueHour by remember(todo.id) {
        mutableStateOf(todo.dueHour)
    }

    var editDueMinute by remember(todo.id) {
        mutableStateOf(todo.dueMinute)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val dueDateText =
        editDueDateMillis?.let { millis ->
            SimpleDateFormat(
                "yyyy년 M월 d일",
                Locale.KOREAN
            ).format(Date(millis))
        } ?: "마감일 없음"

    val dueTimeText =
        if (editDueHour != null && editDueMinute != null) {
            String.format(
                Locale.KOREAN,
                "%02d:%02d",
                editDueHour,
                editDueMinute
            )
        } else {
            "마감 시간 없음"
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("할 일 수정")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = editText,
                    onValueChange = {
                        editText = it
                    },
                    label = {
                        Text("수정할 내용")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("우선순위")

                EditPriorityOption(
                    text = "높음",
                    selected = editPriority == 3,
                    onClick = {
                        editPriority = 3
                    }
                )

                EditPriorityOption(
                    text = "보통",
                    selected = editPriority == 2,
                    onClick = {
                        editPriority = 2
                    }
                )

                EditPriorityOption(
                    text = "낮음",
                    selected = editPriority == 1,
                    onClick = {
                        editPriority = 1
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
                    Text(dueDateText)
                }

                if (editDueDateMillis != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showTimePicker = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(dueTimeText)
                    }

                    if (
                        editDueHour != null &&
                        editDueMinute != null
                    ) {
                        TextButton(
                            onClick = {
                                editDueHour = null
                                editDueMinute = null
                            }
                        ) {
                            Text("마감 시간 삭제")
                        }
                    }

                    TextButton(
                        onClick = {
                            editDueDateMillis = null
                            editDueHour = null
                            editDueMinute = null
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
                    if (editText.isNotBlank()) {
                        onConfirm(
                            editText,
                            editPriority,
                            editDueDateMillis,
                            editDueHour,
                            editDueMinute
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
            initialSelectedDateMillis = editDueDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editDueDateMillis =
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
            initialHour = editDueHour
                ?: currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = editDueMinute
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
                        editDueHour = timePickerState.hour
                        editDueMinute = timePickerState.minute
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
private fun EditPriorityOption(
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