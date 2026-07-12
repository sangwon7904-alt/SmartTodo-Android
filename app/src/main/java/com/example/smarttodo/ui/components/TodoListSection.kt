package com.example.smarttodo.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.smarttodo.data.model.Todo

@Composable
fun TodoListSection(
    todos: List<Todo>,
    selectedFilter: String,
    searchText: String,
    onCheckedChange: (Todo) -> Unit,
    onTodoClick: (Todo) -> Unit,
    onTodoLongClick: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (todos.isEmpty()) {
        EmptyTodoMessage(
            selectedFilter = selectedFilter,
            searchText = searchText,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = todos,
                key = { todo -> todo.id }
            ) { todo ->
                TodoItem(
                    todo = todo,
                    onCheckedChange = {
                        onCheckedChange(todo)
                    },
                    onClick = {
                        onTodoClick(todo)
                    },
                    onLongClick = {
                        onTodoLongClick(todo)
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}