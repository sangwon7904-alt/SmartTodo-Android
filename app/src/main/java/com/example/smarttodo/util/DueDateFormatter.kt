package com.example.smarttodo.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DueDateDisplay(
    val text: String,
    val isOverdue: Boolean
)

fun formatDueDate(
    dueDateMillis: Long?,
    today: LocalDate = LocalDate.now()
): DueDateDisplay? {
    if (dueDateMillis == null) {
        return null
    }

    val dueDate = Instant
        .ofEpochMilli(dueDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val daysDifference = ChronoUnit.DAYS.between(
        today,
        dueDate
    )

    val displayText = when {
        daysDifference < 0 -> {
            "기한 지남 · ${
                dueDate.format(
                    DateTimeFormatter.ofPattern(
                        "M월 d일",
                        Locale.KOREAN
                    )
                )
            }"
        }

        daysDifference == 0L -> "오늘 마감"
        daysDifference == 1L -> "내일 마감"
        daysDifference in 2L..7L -> "${daysDifference}일 남음"

        else -> {
            dueDate.format(
                DateTimeFormatter.ofPattern(
                    "yyyy년 M월 d일",
                    Locale.KOREAN
                )
            )
        }
    }

    return DueDateDisplay(
        text = displayText,
        isOverdue = daysDifference < 0
    )
}