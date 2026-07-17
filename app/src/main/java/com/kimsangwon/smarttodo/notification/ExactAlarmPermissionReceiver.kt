package com.kimsangwon.smarttodo.notification

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kimsangwon.smarttodo.data.storage.TodoStorage

class ExactAlarmPermissionReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            intent.action !=
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
        ) {
            return
        }

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            Log.d(
                "TodoReminder",
                "정확 알람 권한이 아직 허용되지 않음"
            )
            return
        }

        Log.d(
            "TodoReminder",
            "정확 알람 권한 허용됨: 기존 알림 재예약 시작"
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

            var scheduledCount = 0

            todos.forEach { todo ->
                if (!todo.isCompleted) {
                    reminderScheduler.cancel(todo.id)

                    if (reminderScheduler.schedule(todo)) {
                        scheduledCount++
                    }
                }
            }

            Log.d(
                "TodoReminder",
                "정확 알람 재예약 완료: ${scheduledCount}개"
            )
        } catch (exception: Exception) {
            Log.e(
                "TodoReminder",
                "정확 알람 재예약 실패",
                exception
            )
        } finally {
            pendingResult.finish()
        }
    }
}