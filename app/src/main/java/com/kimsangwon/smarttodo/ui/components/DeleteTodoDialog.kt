package com.kimsangwon.smarttodo.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kimsangwon.smarttodo.data.model.Todo

@Composable
fun DeleteTodoDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("할 일 삭제")
        },
        text = {
            Text("'${todo.title}'을 삭제하시겠습니까?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("삭제")
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