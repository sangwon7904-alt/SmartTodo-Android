package com.example.smarttodo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class TodoSnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val todoId = intent.getIntExtra(
            EXTRA_TODO_ID,
            -1
        )

        val todoTitle = intent.getStringExtra(
            EXTRA_TODO_TITLE
        ) ?: "할 일을 확인하세요."

        if (todoId == -1) {
            Log.d(
                "TodoReminder",
                "다시 알림 실패: Todo ID가 없음"
            )
            return
        }

        val triggerTimeMillis =
            System.currentTimeMillis() + SNOOZE_DURATION_MILLIS

        val reminderScheduler = TodoReminderScheduler(
            context.applicationContext
        )

        reminderScheduler.cancel(todoId)

        val scheduled = reminderScheduler.scheduleAt(
            todoId = todoId,
            todoTitle = todoTitle,
            triggerTimeMillis = triggerTimeMillis
        )

        NotificationManagerCompat
            .from(context)
            .cancel(todoId)

        Log.d(
            "TodoReminder",
            if (scheduled) {
                "10분 후 다시 알림 예약 성공: id=$todoId"
            } else {
                "10분 후 다시 알림 예약 실패: id=$todoId"
            }
        )
    }

    companion object {
        const val EXTRA_TODO_ID = "snooze_todo_id"
        const val EXTRA_TODO_TITLE = "snooze_todo_title"

        private const val SNOOZE_DURATION_MILLIS =
            10 * 60 * 1000L
    }
}