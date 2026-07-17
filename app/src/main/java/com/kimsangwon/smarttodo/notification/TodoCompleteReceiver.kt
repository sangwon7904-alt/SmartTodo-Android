package com.kimsangwon.smarttodo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.kimsangwon.smarttodo.data.storage.TodoStorage

class TodoCompleteReceiver : BroadcastReceiver() {

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
                "완료 처리 실패: Todo ID가 없음"
            )
            return
        }

        try {
            val todoStorage = TodoStorage(
                context.applicationContext
            )

            val todos = todoStorage
                .loadTodos()
                .toMutableList()

            val index = todos.indexOfFirst {
                it.id == todoId
            }

            if (index == -1) {
                Log.d(
                    "TodoReminder",
                    "완료 처리 실패: Todo를 찾을 수 없음, id=$todoId"
                )
                return
            }

            val todo = todos[index]

            todos[index] = todo.copy(
                isCompleted = true
            )

            todoStorage.saveTodos(todos)

            val updateIntent = Intent(
                ACTION_TODO_UPDATED
            ).apply {
                setPackage(context.packageName)
            }

            context.sendBroadcast(updateIntent)

            TodoReminderScheduler(
                context.applicationContext
            ).cancel(todoId)

            NotificationManagerCompat
                .from(context)
                .cancel(todoId)

            Log.d(
                "TodoReminder",
                "알림에서 완료 처리 성공: id=$todoId"
            )
        } catch (exception: Exception) {
            Log.e(
                "TodoReminder",
                "알림에서 완료 처리 실패",
                exception
            )
        }
    }

    companion object {
        const val EXTRA_TODO_ID = "complete_todo_id"

        const val ACTION_TODO_UPDATED =
            "com.kimsangwon.smarttodo.action.TODO_UPDATED"
    }
}