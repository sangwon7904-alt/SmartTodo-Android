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

    fun addTodo() {
        todoList.add(
            Todo(
                id = nextId,
                title = "새 할 일 $nextId"
            )
        )
        nextId++
    }
}