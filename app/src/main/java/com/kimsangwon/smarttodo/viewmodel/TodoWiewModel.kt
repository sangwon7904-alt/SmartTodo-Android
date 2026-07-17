package com.kimsangwon.smarttodo.viewmodel

import com.kimsangwon.smarttodo.data.model.Todo
import com.kimsangwon.smarttodo.data.storage.TodoStorage
import com.kimsangwon.smarttodo.notification.TodoReminderScheduler

class TodoViewModel(
    private val todoStorage: TodoStorage,
    private val reminderScheduler: TodoReminderScheduler
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

        val index = todoList.indexOfFirst {
            it.id == todo.id
        }

        if (index != -1) {

            val willBeCompleted = !todo.isCompleted

            val updatedTodo = todo.copy(
                isCompleted = willBeCompleted,
                snoozedUntilMillis =
                    if (willBeCompleted) null
                    else todo.snoozedUntilMillis
            )

            todoList[index] = updatedTodo

            saveTodos()
            updateReminder(updatedTodo)
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
        val newTodo = Todo(
            id = nextId,
            title = title,
            priority = priority,
            dueDateMillis = dueDateMillis,
            dueHour = dueHour,
            dueMinute = dueMinute
        )

        todoList.add(newTodo)

        nextId++

        saveTodos()
        updateReminder(newTodo)
    }

    fun deleteTodo(todo: Todo) {
        reminderScheduler.cancel(todo.id)
        todoList.remove(todo)
        saveTodos()
    }
    fun restoreTodo(todo: Todo) {
        if (todoList.none { it.id == todo.id }) {
            todoList.add(todo)

            saveTodos()
            updateReminder(todo)
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
            val updatedTodo = todo.copy(
                title = newTitle,
                priority = newPriority,
                dueDateMillis = newDueDateMillis,
                dueHour = newDueHour,
                dueMinute = newDueMinute,
                snoozedUntilMillis = null
            )

            todoList[index] = updatedTodo

            saveTodos()
            updateReminder(updatedTodo)
        }
    }
    private fun updateReminder(todo: Todo) {
        reminderScheduler.cancel(todo.id)

        if (!todo.isCompleted) {
            reminderScheduler.schedule(todo)
        }
    }
    fun refreshTodos() {
        val savedTodos = todoStorage.loadTodos()

        todoList.clear()
        todoList.addAll(savedTodos)

        nextId = if (savedTodos.isEmpty()) {
            1
        } else {
            savedTodos.maxOf { it.id } + 1
        }
    }
    private fun saveTodos() {
        todoStorage.saveTodos(todoList)
    }
}