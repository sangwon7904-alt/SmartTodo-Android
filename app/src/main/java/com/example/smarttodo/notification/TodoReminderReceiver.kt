package com.example.smarttodo.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smarttodo.MainActivity

class TodoReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val todoId = intent.getIntExtra(
            EXTRA_TODO_ID,
            0
        )

        val todoTitle = intent.getStringExtra(
            EXTRA_TODO_TITLE
        ) ?: "할 일을 확인하세요."

        val openAppIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            todoId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
        val snoozeIntent = Intent(
            context,
            TodoSnoozeReceiver::class.java
        ).apply {
            putExtra(
                TodoSnoozeReceiver.EXTRA_TODO_ID,
                todoId
            )

            putExtra(
                TodoSnoozeReceiver.EXTRA_TODO_TITLE,
                todoTitle
            )
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId + SNOOZE_REQUEST_CODE_OFFSET,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(
            context,
            TodoCompleteReceiver::class.java
        ).apply {
            putExtra(
                TodoCompleteReceiver.EXTRA_TODO_ID,
                todoId
            )
        }

        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId + COMPLETE_REQUEST_CODE_OFFSET,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            NotificationHelper.CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Smart Todo")
            .setContentText(todoTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "완료",
                completePendingIntent
            )
            .addAction(
                android.R.drawable.ic_popup_reminder,
                "10분 후",
                snoozePendingIntent
            )
            .setAutoCancel(true)
            .build()

        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(todoId, notification)
    }

    companion object {
        const val EXTRA_TODO_ID = "todo_id"
        const val EXTRA_TODO_TITLE = "todo_title"
        private const val COMPLETE_REQUEST_CODE_OFFSET = 100_000
        private const val SNOOZE_REQUEST_CODE_OFFSET = 200_000
    }
}