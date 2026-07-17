package com.example.smarttodo.data.storage

import android.content.Context
import com.example.smarttodo.data.model.Todo
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log
import org.json.JSONException

class TodoStorage(private val context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val TAG = "TodoStorage"
        private const val PREFS_NAME = "todo_prefs"
        private const val TODOS_KEY = "todos"
        private const val CORRUPTED_TODOS_KEY = "corrupted_todos_backup"
    }
    fun saveTodos(todos: List<Todo>): Boolean {
        return try {
            val jsonArray = JSONArray()

            todos.forEach { todo ->
                val jsonObject = JSONObject().apply {
                    put("id", todo.id)
                    put("title", todo.title)
                    put("isCompleted", todo.isCompleted)
                    put("priority", todo.priority)
                    put("dueDateMillis", todo.dueDateMillis)
                    put("dueHour", todo.dueHour)
                    put("dueMinute", todo.dueMinute)
                    put("snoozedUntilMillis", todo.snoozedUntilMillis)
                }

                jsonArray.put(jsonObject)
            }

            prefs.edit()
                .putString(TODOS_KEY, jsonArray.toString())
                .commit()
        } catch (exception: Exception) {
            Log.e(
                TAG,
                "Todo 저장 실패",
                exception
            )

            false
        }
    }

    fun loadTodos(): List<Todo> {
        val jsonString = prefs.getString(
            TODOS_KEY,
            null
        ) ?: return emptyList()

        if (jsonString.isBlank()) {
            return emptyList()
        }

        return try {
            val jsonArray = JSONArray(jsonString)
            val todos = mutableListOf<Todo>()

            for (index in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.optJSONObject(index)

                if (jsonObject == null) {
                    Log.w(
                        TAG,
                        "Todo 항목이 객체가 아니어서 건너뜀: index=$index"
                    )
                    continue
                }

                parseTodo(
                    jsonObject = jsonObject,
                    fallbackId = index + 1
                )?.let { todo ->
                    todos.add(todo)
                }
            }

            todos
        } catch (exception: JSONException) {
            handleCorruptedData(
                jsonString = jsonString,
                exception = exception
            )

            emptyList()
        } catch (exception: Exception) {
            Log.e(
                TAG,
                "Todo 불러오기 실패",
                exception
            )

            emptyList()
        }
    }
    private fun parseTodo(
        jsonObject: JSONObject,
        fallbackId: Int
    ): Todo? {
        val title = jsonObject
            .optString("title", "")
            .trim()

        if (title.isBlank()) {
            Log.w(
                TAG,
                "제목이 없는 Todo를 건너뜀"
            )
            return null
        }

        val id = jsonObject
            .optInt("id", fallbackId)
            .takeIf { it > 0 }
            ?: fallbackId

        val priority = jsonObject
            .optInt("priority", 2)
            .coerceIn(1, 3)

        val dueHour = optionalInt(
            jsonObject = jsonObject,
            key = "dueHour"
        )?.takeIf { it in 0..23 }

        val dueMinute = optionalInt(
            jsonObject = jsonObject,
            key = "dueMinute"
        )?.takeIf { it in 0..59 }

        val dueDateMillis = optionalLong(
            jsonObject = jsonObject,
            key = "dueDateMillis"
        )

        val snoozedUntilMillis = optionalLong(
            jsonObject = jsonObject,
            key = "snoozedUntilMillis"
        )

        return Todo(
            id = id,
            title = title,
            isCompleted = jsonObject.optBoolean(
                "isCompleted",
                false
            ),
            priority = priority,
            dueDateMillis = dueDateMillis,
            dueHour = dueHour,
            dueMinute = dueMinute,
            snoozedUntilMillis = snoozedUntilMillis
        )
    }

    private fun optionalInt(
        jsonObject: JSONObject,
        key: String
    ): Int? {
        if (
            !jsonObject.has(key) ||
            jsonObject.isNull(key)
        ) {
            return null
        }

        return runCatching {
            jsonObject.getInt(key)
        }.getOrNull()
    }

    private fun optionalLong(
        jsonObject: JSONObject,
        key: String
    ): Long? {
        if (
            !jsonObject.has(key) ||
            jsonObject.isNull(key)
        ) {
            return null
        }

        return runCatching {
            jsonObject.getLong(key)
        }.getOrNull()
    }
    private fun handleCorruptedData(
        jsonString: String,
        exception: Exception
    ) {
        Log.e(
            TAG,
            "저장된 Todo JSON이 손상됨. 원본을 백업하고 빈 목록으로 시작함.",
            exception
        )

        prefs.edit()
            .putString(
                CORRUPTED_TODOS_KEY,
                jsonString
            )
            .remove(TODOS_KEY)
            .commit()
    }
}