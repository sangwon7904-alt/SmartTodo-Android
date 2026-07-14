package com.example.smarttodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.smarttodo.ui.screen.MainScreen
import com.example.smarttodo.ui.theme.SmartTodoTheme
import com.example.smarttodo.viewmodel.TodoViewModel
import com.example.smarttodo.data.storage.TodoStorage
import com.example.smarttodo.notification.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)
        setContent {
            SmartTodoTheme {
                val todoStorage = remember { TodoStorage(this@MainActivity) }
                val todoViewModel = remember { TodoViewModel(todoStorage) }
                MainScreen(todoViewModel = todoViewModel)
            }
        }
    }
}