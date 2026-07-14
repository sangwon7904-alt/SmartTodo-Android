package com.example.smarttodo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttodo.data.model.Todo
import com.example.smarttodo.ui.components.AddTodoDialog
import com.example.smarttodo.ui.components.DeleteTodoDialog
import com.example.smarttodo.ui.components.EditTodoDialog
import com.example.smarttodo.ui.components.ProgressSummaryCard
import com.example.smarttodo.ui.components.TodoFilterSection
import com.example.smarttodo.ui.components.TodoListSection
import com.example.smarttodo.ui.components.TodoSearchField
import com.example.smarttodo.util.processTodoList
import com.example.smarttodo.viewmodel.TodoViewModel
import com.example.smarttodo.util.calculateTodoProgress
import kotlinx.coroutines.launch

@Composable
fun MainScreen(todoViewModel: TodoViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    var searchText by remember { mutableStateOf("") }
    var todoToEdit by remember { mutableStateOf<Todo?>(null) }
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

            val todoProgress = calculateTodoProgress(
                todos = todoViewModel.todoList
            )

            val sortedTodoList = processTodoList(
                todos = todoViewModel.todoList,
                searchText = searchText,
                selectedFilter = selectedFilter
            )
            ProgressSummaryCard(
                completedCount = todoProgress.completedCount,
                remainingCount = todoProgress.remainingCount,
                progress = todoProgress.progress,
                progressPercent = todoProgress.progressPercent
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

            TodoListSection(
                todos = sortedTodoList,
                selectedFilter = selectedFilter,
                searchText = searchText,
                onCheckedChange = { todo ->
                    todoViewModel.toggleTodo(todo)
                },
                onTodoClick = { todo ->
                    todoToEdit = todo
                },
                onTodoLongClick = { todo ->
                    todoToDelete = todo
                }
            )
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
        todoToDelete?.let { todo ->
            DeleteTodoDialog(
                todo = todo,
                onDismiss = {
                    todoToDelete = null
                },
                onConfirm = {
                    todoViewModel.deleteTodo(todo)
                    todoToDelete = null

                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "할 일을 삭제했습니다.",
                            actionLabel = "실행 취소",
                            withDismissAction = true
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            todoViewModel.restoreTodo(todo)
                        }
                    }
                }
            )
        }
        todoToEdit?.let { todo ->
            EditTodoDialog(
                todo = todo,
                onDismiss = {
                    todoToEdit = null
                },
                onConfirm = { newTitle, newPriority, newDueDateMillis ->
                    todoViewModel.updateTodo(
                        todo = todo,
                        newTitle = newTitle,
                        newPriority = newPriority,
                        newDueDateMillis = newDueDateMillis
                    )

                    todoToEdit = null
                }
            )
        }
    }
}