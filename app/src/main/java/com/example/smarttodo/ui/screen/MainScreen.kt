package com.example.smarttodo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import com.example.smarttodo.ui.components.ProgressSummaryCard
import com.example.smarttodo.ui.components.TodoSearchField
import com.example.smarttodo.ui.components.TodoFilterSection
import com.example.smarttodo.viewmodel.TodoViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.smarttodo.ui.components.AddTodoDialog

@Composable
fun MainScreen(todoViewModel: TodoViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    var searchText by remember { mutableStateOf("") }
    var todoToEdit by remember { mutableStateOf<Todo?>(null) }
    var editText by remember { mutableStateOf("") }
    var editPriority by remember { mutableStateOf(2) }
    var selectedFilter by remember { mutableStateOf("전체") }
    val filterOptions = listOf("전체", "미완료", "완료")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
                text = {
                    Text("새 할 일")
                },
                icon = {
                    Text(
                        text = "+",
                        fontSize = 24.sp
                    )
                }
            )
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
            val progress = if (totalCount == 0) {
                0f
            } else {
                completedCount.toFloat() / totalCount.toFloat()
            }

            val progressPercent = (progress * 100).toInt()

            val filteredTodoList = todoViewModel.todoList.filter { todo ->
                val matchesSearch =
                    todo.title.contains(searchText, ignoreCase = true)

                val matchesFilter = when (selectedFilter) {
                    "미완료" -> !todo.isCompleted
                    "완료" -> todo.isCompleted
                    else -> true
                }

                matchesSearch && matchesFilter
            }

            val sortedTodoList = filteredTodoList.sortedWith(
                compareBy<Todo> {
                    if (it.isCompleted) 1 else 0
                }.thenByDescending {
                    it.priority
                }.thenByDescending {
                    it.id
                }
            )
            ProgressSummaryCard(
                completedCount = completedCount,
                remainingCount = remainingCount,
                progress = progress,
                progressPercent = progressPercent
            )

            Spacer(modifier = Modifier.height(16.dp))

            TodoSearchField(
                searchText = searchText,
                onSearchTextChange = {
                    searchText = it
                },
                onClearClick = {
                    searchText = ""
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            TodoFilterSection(
                selectedFilter = selectedFilter,
                filterOptions = filterOptions,
                onFilterSelected = {
                    selectedFilter = it
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (sortedTodoList.isEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = when (selectedFilter) {
                        "미완료" -> "남아 있는 할 일이 없습니다."
                        "완료" -> "완료한 할 일이 없습니다."
                        else -> "아직 할 일이 없습니다."
                    },
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedFilter == "전체" && searchText.isBlank()) {
                    Text(
                        text = "+ 버튼을 눌러 새 할 일을 추가하세요.",
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn {
                    items(
                        items = sortedTodoList,
                        key = { todo -> todo.id }
                    ) { todo ->
                        TodoItem(
                            todo = todo,
                            onCheckedChange = {
                                todoViewModel.toggleTodo(todo)
                            },
                            onClick = {
                                todoToEdit = todo
                                editText = todo.title
                                editPriority = todo.priority
                            },
                            onLongClick = {
                                todoToDelete = todo
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
        if (showAddDialog) {
            AddTodoDialog(
                onDismiss = {
                    showAddDialog = false
                },
                onConfirm = { title, priority, dueDateMillis ->
                    todoViewModel.addTodo(
                        title = title,
                        priority = priority,
                        dueDateMillis = dueDateMillis
                    )

                    showAddDialog = false
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
                            val deletedTodo = todoToDelete

                            if (deletedTodo != null) {
                                todoViewModel.deleteTodo(deletedTodo)
                                todoToDelete = null

                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "할 일을 삭제했습니다.",
                                        actionLabel = "실행 취소",
                                        withDismissAction = true
                                    )

                                    if (result == SnackbarResult.ActionPerformed) {
                                        todoViewModel.restoreTodo(deletedTodo)
                                    }
                                }
                            }
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
                    editPriority = 2
                },
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

                        PriorityOption(
                            text = "높음",
                            selected = editPriority == 3,
                            onClick = {
                                editPriority = 3
                            }
                        )

                        PriorityOption(
                            text = "보통",
                            selected = editPriority == 2,
                            onClick = {
                                editPriority = 2
                            }
                        )

                        PriorityOption(
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
                            todoToEdit?.let {
                                todoViewModel.updateTodo(
                                    todo = it,
                                    newTitle = editText,
                                    newPriority = editPriority
                                )
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
                            editPriority = 2
                        }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
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