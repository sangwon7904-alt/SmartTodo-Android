package com.kimsangwon.smarttodo.util

import com.kimsangwon.smarttodo.data.model.Todo
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun calculateReminderTimeMillis(
    todo: Todo,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long? {
    val dueDateMillis = todo.dueDateMillis ?: return null
    val dueHour = todo.dueHour ?: return null
    val dueMinute = todo.dueMinute ?: return null

    if (dueHour !in 0..23 || dueMinute !in 0..59) {
        return null
    }

    val dueDate: LocalDate = Instant
        .ofEpochMilli(dueDateMillis)
        .atZone(zoneId)
        .toLocalDate()

    val dueTime = LocalTime.of(
        dueHour,
        dueMinute
    )

    val dueDateTime = LocalDateTime.of(
        dueDate,
        dueTime
    )

    return dueDateTime
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
}
