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
import com.example.smarttodo.data.model.Todo
import com.example.smarttodo.ui.components.TodoItem
import com.example.smarttodo.viewmodel.TodoViewModel

@Composable
fun MainScreen(todoViewModel: TodoViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var todoText by remember { mutableStateOf("") }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    var searchText by remember { mutableStateOf("") }
    var todoToEdit by remember { mutableStateOf<Todo?>(null) }
    var editText by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
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

            val totalCount = todoViewModel.todoList.size
            val completedCount = todoViewModel.todoList.count { it.isCompleted }
            val remainingCount = totalCount - completedCount

            val filteredTodoList = todoViewModel.todoList.filter {
                it.title.contains(searchText, ignoreCase = true)
            }

            val sortedTodoList = filteredTodoList.sortedBy { it.isCompleted }

            Text(
                text = "전체 ${totalCount}개 · 완료 ${completedCount}개 · 남은 ${remainingCount}개",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                label = {
                    Text("검색")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (sortedTodoList.isEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "아직 할 일이 없습니다.",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "+ 버튼을 눌러 새 할 일을 추가하세요.",
                    fontSize = 14.sp
                )
            } else {
                LazyColumn {
                    items(sortedTodoList) { todo ->
                        TodoItem(
                            todo = todo,
                            onCheckedChange = {
                                todoViewModel.toggleTodo(todo)
                            },
                            onClick = {
                                todoToEdit = todo
                                editText = todo.title
                            },
                            onLongClick = {
                                todoToDelete = todo
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
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
                            showAddDialog = false
                        }
                    ) {
                        Text("저장")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            todoText = ""
                            showAddDialog = false
                        }
                    ) {
                        Text("취소")
                    }
                }
            )
        }

        if (todoToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    todoToDelete = null
                },
                title = {
                    Text("할 일 삭제")
                },
                text = {
                    Text("'${todoToDelete?.title}'을 삭제하시겠습니까?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            todoToDelete?.let {
                                todoViewModel.deleteTodo(it)
                            }
                            todoToDelete = null
                        }
                    ) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            todoToDelete = null
                        }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
        if (todoToEdit != null) {
            AlertDialog(
                onDismissRequest = {
                    todoToEdit = null
                    editText = ""
                },
                title = {
                    Text("할 일 수정")
                },
                text = {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = {
                            editText = it
                        },
                        label = {
                            Text("수정할 내용")
                        },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            todoToEdit?.let {
                                todoViewModel.updateTodo(it, editText)
                            }
                            todoToEdit = null
                            editText = ""
                        }
                    ) {
                        Text("저장")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            todoToEdit = null
                            editText = ""
                        }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }
}