package com.example.smarttodo.data.storage

import android.content.Context
import com.example.smarttodo.data.model.Todo
import org.json.JSONArray
import org.json.JSONObject

class TodoStorage(private val context: Context) {

    private val prefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

    fun saveTodos(todos: List<Todo>) {
        val jsonArray = JSONArray()

        todos.forEach { todo ->
            val jsonObject = JSONObject()
            jsonObject.put("id", todo.id)
            jsonObject.put("title", todo.title)
            jsonObject.put("isCompleted", todo.isCompleted)
            jsonArray.put(jsonObject)
            jsonObject.put("priority", todo.priority)
        }

        prefs.edit()
            .putString("todos", jsonArray.toString())
            .apply()
    }

    fun loadTodos(): List<Todo> {
        val jsonString = prefs.getString("todos", null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val todos = mutableListOf<Todo>()


        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            todos.add(
                Todo(
                    id = jsonObject.getInt("id"),
                    title = jsonObject.getString("title"),
                    isCompleted = jsonObject.getBoolean("isCompleted"),
                    priority = jsonObject.optInt("priority", 2)
                )
            )
        }

        return todos
    }
}