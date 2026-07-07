package com.example.smarttodo.data.model

data class Todo(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)