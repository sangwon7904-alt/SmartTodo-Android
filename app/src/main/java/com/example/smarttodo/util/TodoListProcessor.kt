package com.example.smarttodo.util

import com.example.smarttodo.data.model.Todo

fun processTodoList(
    todos: List<Todo>,
    searchText: String,
    selectedFilter: String
): List<Todo> {
    return todos
        .filter { todo ->
            val matchesSearch = todo.title.contains(
                other = searchText,
                ignoreCase = true
            )

            val matchesFilter = when (selectedFilter) {
                "미완료" -> !todo.isCompleted
                "완료" -> todo.isCompleted
                else -> true
            }

            matchesSearch && matchesFilter
        }
        .sortedWith(
            compareBy<Todo> {
                if (it.isCompleted) 1 else 0
            }.thenByDescending {
                it.priority
            }.thenByDescending {
                it.id
            }
        )
}