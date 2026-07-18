package com.kimsangwon.smarttodo.ui.screen

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
import com.kimsangwon.smarttodo.data.model.Todo
import com.kimsangwon.smarttodo.ui.components.AddTodoDialog
import com.kimsangwon.smarttodo.ui.components.DeleteTodoDialog
import com.kimsangwon.smarttodo.ui.components.EditTodoDialog
import com.kimsangwon.smarttodo.ui.components.ProgressSummaryCard
import com.kimsangwon.smarttodo.ui.components.TodoFilterSection
import com.kimsangwon.smarttodo.ui.components.TodoListSection
import com.kimsangwon.smarttodo.ui.components.TodoSearchField
import com.kimsangwon.smarttodo.util.processTodoList
import com.kimsangwon.smarttodo.viewmodel.TodoViewModel
import com.kimsangwon.smarttodo.util.calculateTodoProgress
import kotlinx.coroutines.launch
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.kimsangwon.smarttodo.notification.TodoCompleteReceiver
import com.kimsangwon.smarttodo.ui.components.EmptyTodoView

@Composable
fun MainScreen(todoViewModel: TodoViewModel) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    var searchText by remember { mutableStateOf("") }
    var todoToEdit by remember { mutableStateOf<Todo?>(null) }
    var selectedFilter by remember { mutableStateOf("전체") }
    val filterOptions = listOf("전체", "오늘", "미완료", "완료")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(context, todoViewModel) {
        val todoUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                receiverContext: Context?,
                intent: Intent?
            ) {
                if (
                    intent?.action ==
                    TodoCompleteReceiver.ACTION_TODO_UPDATED
                ) {
                    todoViewModel.refreshTodos()
                }
            }
        }

        val intentFilter = IntentFilter(
            TodoCompleteReceiver.ACTION_TODO_UPDATED
        )

        ContextCompat.registerReceiver(
            context,
            todoUpdateReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(todoUpdateReceiver)
        }
    }

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

            if (sortedTodoList.isEmpty()) {
                EmptyTodoView(
                    hasAnyTodo = todoViewModel.todoList.isNotEmpty(),
                    selectedFilter = selectedFilter,
                    searchText = searchText
                )
            } else {
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
        }
        if (showAddDialog) {
            AddTodoDialog(
                onDismiss = {
                    showAddDialog = false
                },
                onConfirm = {
                        title,
                        priority,
                        dueDateMillis,
                        dueHour,
                        dueMinute ->

                    todoViewModel.addTodo(
                        title = title,
                        priority = priority,
                        dueDateMillis = dueDateMillis,
                        dueHour = dueHour,
                        dueMinute = dueMinute
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
                onConfirm = {
                        newTitle,
                        newPriority,
                        newDueDateMillis,
                        newDueHour,
                        newDueMinute ->

                    todoViewModel.updateTodo(
                        todo = todo,
                        newTitle = newTitle,
                        newPriority = newPriority,
                        newDueDateMillis = newDueDateMillis,
                        newDueHour = newDueHour,
                        newDueMinute = newDueMinute
                    )

                    todoToEdit = null
                }
            )
        }


    }
}