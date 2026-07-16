package com.example.smarttodo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.smarttodo.data.storage.TodoStorage

class TodoSnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val todoId = intent.getIntExtra(
            EXTRA_TODO_ID,
            -1
        )

        if (todoId == -1) {
            Log.d(
                "TodoReminder",
                "다시 알림 실패: Todo ID가 없음"
            )
            return
        }

        val todoStorage = TodoStorage(
            context.applicationContext
        )

        val todos = todoStorage
            .loadTodos()
            .toMutableList()

        val todoIndex = todos.indexOfFirst {
            it.id == todoId
        }

        if (todoIndex == -1) {
            Log.d(
                "TodoReminder",
                "다시 알림 실패: Todo가 삭제됨, id=$todoId"
            )

            NotificationManagerCompat
                .from(context)
                .cancel(todoId)

            return
        }

        val todo = todos[todoIndex]

        if (todo.isCompleted) {
            Log.d(
                "TodoReminder",
                "다시 알림 취소: 이미 완료된 Todo, id=$todoId"
            )

            NotificationManagerCompat
                .from(context)
                .cancel(todoId)

            return
        }

        val triggerTimeMillis =
            System.currentTimeMillis() + SNOOZE_DURATION_MILLIS

        val updatedTodo = todo.copy(
            snoozedUntilMillis = triggerTimeMillis
        )

        todos[todoIndex] = updatedTodo
        todoStorage.saveTodos(todos)

        val reminderScheduler = TodoReminderScheduler(
            context.applicationContext
        )

        reminderScheduler.cancel(todoId)

        val scheduled = reminderScheduler.scheduleAt(
            todoId = updatedTodo.id,
            todoTitle = updatedTodo.title,
            triggerTimeMillis = triggerTimeMillis
        )

        NotificationManagerCompat
            .from(context)
            .cancel(todoId)

        sendTodoUpdatedBroadcast(context)

        Log.d(
            "TodoReminder",
            if (scheduled) {
                "10분 후 다시 알림 예약 성공: id=$todoId"
            } else {
                "10분 후 다시 알림 예약 실패: id=$todoId"
            }
        )
    }

    private fun sendTodoUpdatedBroadcast(
        context: Context
    ) {
        val updateIntent = Intent(
            TodoCompleteReceiver.ACTION_TODO_UPDATED
        ).apply {
            setPackage(context.packageName)
        }

        context.sendBroadcast(updateIntent)
    }

    companion object {
        const val EXTRA_TODO_ID = "snooze_todo_id"
        const val EXTRA_TODO_TITLE = "snooze_todo_title"

        private const val SNOOZE_DURATION_MILLIS =
            10 * 60 * 1000L
    }
}