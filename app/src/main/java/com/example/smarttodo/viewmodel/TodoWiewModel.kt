package com.example.smarttodo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.example.smarttodo.data.model.Todo

class TodoViewModel {

    private var nextId = 5

    val todoList = mutableStateListOf(
        Todo(1, "운동하기"),
        Todo(2, "영어 공부"),
        Todo(3, "장보기"),
        Todo(4, "독서하기")
    )

    fun toggleTodo(todo: Todo) {
        val index = todoList.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            todoList[index] = todo.copy(isCompleted = !todo.isCompleted)
        }
    }

    fun addTodo(title: String) {
        if (title.isBlank()) return

        todoList.add(
            Todo(
                id = nextId,
                title = title
            )
        )
        nextId++
    }
    fun deleteTodo(todo: Todo) {
        todoList.remove(todo)
    }
}