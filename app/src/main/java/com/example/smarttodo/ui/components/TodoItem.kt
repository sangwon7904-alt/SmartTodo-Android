package com.example.smarttodo.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttodo.data.model.Todo
import com.example.smarttodo.util.formatDueDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItem(
    todo: Todo,
    onCheckedChange: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dueDateDisplay = formatDueDate(
        dueDateMillis = todo.dueDateMillis,
        dueHour = todo.dueHour,
        dueMinute = todo.dueMinute
    )
    val snoozeText =
        todo.snoozedUntilMillis
            ?.takeIf {
                it > System.currentTimeMillis()
            }
            ?.let { millis ->
                val snoozeTime = Instant
                    .ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()

                val formattedTime = snoozeTime.format(
                    DateTimeFormatter.ofPattern(
                        "HH:mm",
                        Locale.KOREAN
                    )
                )

                "⏰ ${formattedTime}에 다시 알림"
            }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = {
                    onCheckedChange()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = when (todo.priority) {
                    3 -> "🔴"
                    2 -> "🟡"
                    else -> "🟢"
                },
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todo.title,
                    fontSize = 17.sp,
                    textDecoration = if (todo.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    color = if (todo.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = when (todo.priority) {
                        3 -> "높은 우선순위"
                        2 -> "보통 우선순위"
                        else -> "낮은 우선순위"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (dueDateDisplay != null) {
                    Text(
                        text = dueDateDisplay.text,
                        fontSize = 12.sp,
                        color = if (
                            dueDateDisplay.isOverdue &&
                            !todo.isCompleted
                        ) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                if (snoozeText != null && !todo.isCompleted) {
                    Text(
                        text = snoozeText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}