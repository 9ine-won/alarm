package com.example.alarmgame.ui.screen.settings

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var notificationsAllowed by remember { mutableStateOf(notificationsEnabled(context)) }
    var exactAlarmAllowed by remember { mutableStateOf(canScheduleExactAlarms(context)) }
    var ignoringBatteryOpt by remember { mutableStateOf(isIgnoringBatteryOpt(context)) }
    val background = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.background
        )
    )

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsAllowed = granted || notificationsEnabled(context)
    }

    LaunchedEffect(Unit) {
        notificationsAllowed = notificationsEnabled(context)
        exactAlarmAllowed = canScheduleExactAlarms(context)
        ignoringBatteryOpt = isIgnoringBatteryOpt(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("설정") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsHeader()
                PermissionCard(
                    title = "알림 권한",
                    description = "Android 13+에서 알람 알림을 울리기 위해 필요합니다.",
                    status = if (notificationsAllowed) "허용됨" else "필요",
                    actionText = if (notificationsAllowed) "설정 열기" else "권한 요청",
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            openAppNotificationSettings(context)
                        }
                    }
                )
                PermissionCard(
                    title = "정확 알람",
                    description = "Android 12+에서 정확 알람을 보장하려면 설정에서 허용이 필요할 수 있습니다.",
                    status = if (exactAlarmAllowed) "허용됨" else "설정 필요",
                    actionText = "설정 열기",
                    onAction = { openExactAlarmSettings(context) }
                )
                PermissionCard(
                    title = "배터리 최적화 예외",
                    description = "Doze 중 알람 누락을 줄이기 위해 배터리 최적화 예외 설정을 권장합니다.",
                    status = if (ignoringBatteryOpt) "예외 적용" else "권장",
                    actionText = "설정 열기",
                    onAction = { openBatteryOptimizationSettings(context) }
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    val gradient = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(gradient, shape = RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("권한 및 안정성", color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    "알람 누락 없이 울리도록 권한과 설정을 점검하세요.",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    status: String,
    actionText: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("상태: $status", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}

private fun notificationsEnabled(context: Context): Boolean {
    val nm = context.getSystemService(NotificationManager::class.java)
    return nm?.areNotificationsEnabled() == true
}

private fun canScheduleExactAlarms(context: Context): Boolean {
    val am = context.getSystemService(AlarmManager::class.java) ?: return false
    return am.canScheduleExactAlarms()
}

private fun isIgnoringBatteryOpt(context: Context): Boolean {
    val pm = context.getSystemService(PowerManager::class.java) ?: return false
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

private fun openAppNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

private fun openBatteryOptimizationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
