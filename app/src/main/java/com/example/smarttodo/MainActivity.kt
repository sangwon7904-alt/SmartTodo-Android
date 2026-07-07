package com.example.smarttodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.smarttodo.ui.screen.MainScreen
import com.example.smarttodo.ui.theme.SmartTodoTheme
import com.example.smarttodo.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTodoTheme {
                val todoViewModel = remember { TodoViewModel() }
                MainScreen(todoViewModel = todoViewModel)
            }
        }
    }
}