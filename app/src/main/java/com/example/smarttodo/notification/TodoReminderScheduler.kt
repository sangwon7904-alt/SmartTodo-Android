package com.example.smarttodo.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.smarttodo.data.model.Todo
import com.example.smarttodo.util.calculateReminderTimeMillis

class TodoReminderScheduler(
    private val context: Context
) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(todo: Todo): Boolean {
        val reminderTimeMillis =
            calculateReminderTimeMillis(todo) ?: return false

        if (reminderTimeMillis <= System.currentTimeMillis()) {
            return false
        }

        val pendingIntent = createPendingIntent(todo)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                pendingIntent
            )
        }

        return true
    }

    fun cancel(todoId: Int) {
        val pendingIntent = createPendingIntent(
            todoId = todoId,
            todoTitle = ""
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(todo: Todo): PendingIntent {
        return createPendingIntent(
            todoId = todo.id,
            todoTitle = todo.title
        )
    }

    private fun createPendingIntent(
        todoId: Int,
        todoTitle: String
    ): PendingIntent {
        val intent = Intent(
            context,
            TodoReminderReceiver::class.java
        ).apply {
            putExtra(
                TodoReminderReceiver.EXTRA_TODO_ID,
                todoId
            )

            putExtra(
                TodoReminderReceiver.EXTRA_TODO_TITLE,
                todoTitle
            )
        }

        return PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}