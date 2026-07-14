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
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)
        setContent {
            SmartTodoTheme {
                val notificationPermissionLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        // 지금은 결과에 따라 별도 동작을 하지 않습니다.
                        // 이후 설정 화면에서 권한 상태 안내를 추가할 수 있습니다.
                    }

                LaunchedEffect(Unit) {
                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
                val todoStorage = remember { TodoStorage(this@MainActivity) }
                val todoViewModel = remember { TodoViewModel(todoStorage) }
                MainScreen(todoViewModel = todoViewModel)
            }
        }
    }
}