package com.example.smarttodo.viewmodel

import com.example.smarttodo.data.model.Todo
import com.example.smarttodo.data.storage.TodoStorage

class TodoViewModel(
    private val todoStorage: TodoStorage
) {
    private var nextId = 1

    val todoList = androidx.compose.runtime.mutableStateListOf<Todo>()

    init {
        val savedTodos = todoStorage.loadTodos()
        todoList.addAll(savedTodos)

        nextId = if (savedTodos.isEmpty()) {
            1
        } else {
            savedTodos.maxOf { it.id } + 1
        }
    }

    fun toggleTodo(todo: Todo) {
        val index = todoList.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            todoList[index] = todo.copy(isCompleted = !todo.isCompleted)
            saveTodos()
        }
    }

    fun addTodo(title: String, priority: Int = 2) {
        if (title.isBlank()) return

        todoList.add(
            Todo(
                id = nextId,
                title = title,
                priority = priority
            )
        )
        nextId++
        saveTodos()
    }

    fun deleteTodo(todo: Todo) {
        todoList.remove(todo)
        saveTodos()
    }
    fun restoreTodo(todo: Todo) {
        if (todoList.none { it.id == todo.id }) {
            todoList.add(todo)
            saveTodos()
        }
    }

    fun updateTodo(
        todo: Todo,
        newTitle: String,
        newPriority: Int
    ) {
        if (newTitle.isBlank()) return

        val index = todoList.indexOfFirst { it.id == todo.id }

        if (index != -1) {
            todoList[index] = todo.copy(
                title = newTitle,
                priority = newPriority
            )

            saveTodos()
        }
    }

    private fun saveTodos() {
        todoStorage.saveTodos(todoList)
    }
}