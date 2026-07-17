package com.kimsangwon.smarttodo.util

import com.kimsangwon.smarttodo.data.model.Todo

data class TodoProgress(
    val totalCount: Int,
    val completedCount: Int,
    val remainingCount: Int,
    val progress: Float,
    val progressPercent: Int
)

fun calculateTodoProgress(
    todos: List<Todo>
): TodoProgress {
    val totalCount = todos.size
    val completedCount = todos.count { it.isCompleted }
    val remainingCount = totalCount - completedCount

    val progress = if (totalCount == 0) {
        0f
    } else {
        completedCount.toFloat() / totalCount.toFloat()
    }

    val progressPercent = (progress * 100).toInt()

    return TodoProgress(
        totalCount = totalCount,
        completedCount = completedCount,
        remainingCount = remainingCount,
        progress = progress,
        progressPercent = progressPercent
    )
}