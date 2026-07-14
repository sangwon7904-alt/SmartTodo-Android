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

    fun addTodo(
        title: String,
        priority: Int = 2,
        dueDateMillis: Long? = null,
        dueHour: Int? = null,
        dueMinute: Int? = null
    ) {
        if (title.isBlank()) return

        todoList.add(
            Todo(
                id = nextId,
                title = title,
                priority = priority,
                dueDateMillis = dueDateMillis,
                dueHour = dueHour,
                dueMinute = dueMinute
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
        newPriority: Int,
        newDueDateMillis: Long? = todo.dueDateMillis,
        newDueHour: Int? = todo.dueHour,
        newDueMinute: Int? = todo.dueMinute

    ) {
        if (newTitle.isBlank()) return

        val index = todoList.indexOfFirst { it.id == todo.id }

        if (index != -1) {
            todoList[index] = todo.copy(
                title = newTitle,
                priority = newPriority,
                dueDateMillis = newDueDateMillis,
                dueHour = newDueHour,
                dueMinute = newDueMinute

            )

            saveTodos()
        }
    }


    private fun saveTodos() {
        todoStorage.saveTodos(todoList)
    }
}