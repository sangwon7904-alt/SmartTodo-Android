package com.kimsangwon.smarttodo.data.model

data class Todo(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Int = 2,
    val dueDateMillis: Long? = null,
    val dueHour: Int? = null,
    val dueMinute: Int? = null,
    val snoozedUntilMillis: Long? = null
)