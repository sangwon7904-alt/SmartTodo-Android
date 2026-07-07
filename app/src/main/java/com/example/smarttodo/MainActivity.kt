package com.example.smarttodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttodo.ui.theme.SmartTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTodoTheme {
                SmartTodoApp()
            }
        }
    }
}

@Composable
fun SmartTodoApp() {
    val todoList = listOf(
        "운동하기",
        "영어 공부",
        "장보기",
        "독서하기"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }
            ) {
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

@Composable
fun TodoItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            fontSize = 18.sp
        )
    }
}