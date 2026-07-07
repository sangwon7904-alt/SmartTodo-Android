package com.example.smarttodo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttodo.ui.components.TodoItem
import com.example.smarttodo.viewmodel.TodoViewModel

@Composable
fun MainScreen(todoViewModel: TodoViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var todoText by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Text("+", fontSize = 28.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "📋 Smart Todo",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "오늘 해야 할 일",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(todoViewModel.todoList) { todo ->
                    TodoItem(
                        todo = todo,
                        onCheckedChange = {
                            todoViewModel.toggleTodo(todo)
                        }
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    todoText = ""
                },
                title = {
                    Text("새 할 일 추가")
                },
                text = {
                    OutlinedTextField(
                        value = todoText,
                        onValueChange = {
                            todoText = it
                        },
                        label = {
                            Text("할 일을 입력하세요")
                        },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            todoViewModel.addTodo(todoText)
                            todoText = ""
                            showDialog = false
                        }
                    ) {
                        Text("저장")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            todoText = ""
                            showDialog = false
                        }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }
}