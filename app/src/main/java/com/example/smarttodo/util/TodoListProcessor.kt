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
                "미완료" -> !todo.isCompleted
                "완료" -> todo.isCompleted
                else -> true
            }

            matchesSearch && matchesFilter
        }
        .sortedWith(
            compareBy<Todo> {
                // 미완료 항목을 완료 항목보다 위에 표시
                if (it.isCompleted) 1 else 0
            }.thenBy {
                // 미완료 항목 안에서 마감일 순서 계산
                dueDateSortGroup(
                    todo = it,
                    today = today,
                    zoneId = zoneId
                )
            }.thenBy {
                // 실제 마감일이 빠른 항목부터 표시
                it.dueDateMillis ?: Long.MAX_VALUE
            }.thenByDescending {
                // 마감일 조건이 같으면 높은 우선순위부터 표시
                it.priority
            }.thenByDescending {
                // 나머지 조건도 같으면 최근 추가 항목부터 표시
                it.id
            }
        )
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