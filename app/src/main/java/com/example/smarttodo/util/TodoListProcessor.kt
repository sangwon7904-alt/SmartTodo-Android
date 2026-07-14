package com.example.smarttodo.util

import com.example.smarttodo.data.model.Todo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun processTodoList(
    todos: List<Todo>,
    searchText: String,
    selectedFilter: String
): List<Todo> {
    val today = LocalDate.now()
    val zoneId = ZoneId.systemDefault()

    return todos
        .filter { todo ->
            val matchesSearch = todo.title.contains(
                other = searchText,
                ignoreCase = true
            )

            val matchesFilter = when (selectedFilter) {
                "오늘" -> {
                    isDueTodayOrOverdue(
                        todo = todo,
                        today = today,
                        zoneId = zoneId
                    )
                }

                "미완료" -> !todo.isCompleted
                "완료" -> todo.isCompleted
                else -> true
            }

            matchesSearch && matchesFilter
        }
        .sortedWith(
            compareBy<Todo> {
                if (it.isCompleted) 1 else 0
            }.thenBy {
                dueDateSortGroup(
                    todo = it,
                    today = today,
                    zoneId = zoneId
                )
            }.thenBy {
                it.dueDateMillis ?: Long.MAX_VALUE
            }.thenByDescending {
                it.priority
            }.thenByDescending {
                it.id
            }
        )
}

private fun isDueTodayOrOverdue(
    todo: Todo,
    today: LocalDate,
    zoneId: ZoneId
): Boolean {
    if (todo.isCompleted) {
        return false
    }

    val dueDateMillis = todo.dueDateMillis ?: return false

    val dueDate = Instant
        .ofEpochMilli(dueDateMillis)
        .atZone(zoneId)
        .toLocalDate()

    return !dueDate.isAfter(today)
}

private fun dueDateSortGroup(
    todo: Todo,
    today: LocalDate,
    zoneId: ZoneId
): Int {
    if (todo.isCompleted) {
        return 4
    }

    val dueDateMillis = todo.dueDateMillis ?: return 3

    val dueDate = Instant
        .ofEpochMilli(dueDateMillis)
        .atZone(zoneId)
        .toLocalDate()

    return when {
        dueDate.isBefore(today) -> 0
        dueDate.isEqual(today) -> 1
        else -> 2
    }
}