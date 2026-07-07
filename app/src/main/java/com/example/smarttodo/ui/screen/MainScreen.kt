package com.example.smarttodo.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttodo.ui.components.TodoItem

@Composable
fun MainScreen() {
    val todoList = listOf(
        "운동하기",
        "영어 공부",
        "장보기",
        "독서하기"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Text("+", fontSize = 28.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "📋 Smart Todo",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "오늘 해야 할 일",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(todoList) { todo ->
                    TodoItem(title = todo)
                }
            }
        }
    }
}