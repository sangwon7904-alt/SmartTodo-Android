package com.example.smarttodo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smarttodo.data.storage.TodoStorage

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            return
        }

        Log.d(
            "TodoReminder",
            "알림 복원 시작: action=${intent.action}"
        )

        val pendingResult = goAsync()

        try {
            val todoStorage = TodoStorage(
                context.applicationContext
            )

            val reminderScheduler = TodoReminderScheduler(
                context.applicationContext
            )

            val todos = todoStorage.loadTodos()

            var restoredCount = 0

            todos.forEach { todo ->
                if (!todo.isCompleted) {
                    val scheduled =
                        reminderScheduler.schedule(todo)

                    if (scheduled) {
                        restoredCount++
                    }
                }
            }

            Log.d(
                "TodoReminder",
                "알림 복원 완료: ${restoredCount}개"
            )
        } catch (exception: Exception) {
            Log.e(
                "TodoReminder",
                "알림 복원 실패",
                exception
            )
        } finally {
            pendingResult.finish()
        }
    }
}