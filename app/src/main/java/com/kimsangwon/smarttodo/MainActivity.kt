package com.kimsangwon.smarttodo

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.kimsangwon.smarttodo.ui.screen.MainScreen
import com.kimsangwon.smarttodo.ui.theme.SmartTodoTheme
import com.kimsangwon.smarttodo.viewmodel.TodoViewModel
import com.kimsangwon.smarttodo.data.storage.TodoStorage
import com.kimsangwon.smarttodo.notification.NotificationHelper
import com.kimsangwon.smarttodo.notification.TodoReminderScheduler
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
                var showExactAlarmDialog by remember {
                    mutableStateOf(false)
                }
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager =
                            getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        if (!alarmManager.canScheduleExactAlarms()) {
                            showExactAlarmDialog = true
                        }
                    }
                }
                val todoStorage = remember {
                    TodoStorage(this@MainActivity)
                }

                val reminderScheduler = remember {
                    TodoReminderScheduler(this@MainActivity)
                }

                val todoViewModel = remember {
                    TodoViewModel(
                        todoStorage = todoStorage,
                        reminderScheduler = reminderScheduler
                    )
                }
                MainScreen(todoViewModel = todoViewModel)
                if (showExactAlarmDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showExactAlarmDialog = false
                        },
                        title = {
                            Text("정확한 알림 설정")
                        },
                        text = {
                            Text(
                                "SmartTodo가 설정한 시간에 정확히 알림을 보내려면 " +
                                        "'알람 및 리마인더' 권한이 필요합니다."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showExactAlarmDialog = false

                                    val intent = Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                    ).apply {
                                        data = Uri.parse(
                                            "package:$packageName"
                                        )
                                    }

                                    startActivity(intent)
                                }
                            ) {
                                Text("설정 열기")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showExactAlarmDialog = false
                                }
                            ) {
                                Text("나중에")
                            }
                        }
                    )
                }
            }
        }
    }
}