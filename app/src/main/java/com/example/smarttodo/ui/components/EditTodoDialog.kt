package com.example.smarttodo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarttodo.data.model.Todo

@Composable
fun EditTodoDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        priority: Int
    ) -> Unit
) {
    var editText by remember(todo.id) {
        mutableStateOf(todo.title)
    }

    var editPriority by remember(todo.id) {
        mutableIntStateOf(todo.priority)
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (editText.isNotBlank()) {
                        onConfirm(
                            editText,
                            editPriority
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