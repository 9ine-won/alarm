package com.example.alarmgame.ui.screen.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.alarmgame.platform.PermissionManager

@Composable
fun PermissionScreen(
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var notificationGranted by remember { mutableStateOf(PermissionManager.hasNotificationPermission(context)) }
    var exactAlarmGranted by remember { mutableStateOf(PermissionManager.hasExactAlarmPermission(context)) }
    var overlayGranted by remember { mutableStateOf(PermissionManager.hasOverlayPermission(context)) }

    // 화면이 다시 포커스를 얻을 때마다 권한 상태를 다시 확인합니다.
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            notificationGranted = PermissionManager.hasNotificationPermission(context)
            exactAlarmGranted = PermissionManager.hasExactAlarmPermission(context)
            overlayGranted = PermissionManager.hasOverlayPermission(context)

            if (notificationGranted && exactAlarmGranted && overlayGranted) {
                onAllPermissionsGranted()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "앱 권한 설정",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "알람이 정상적으로 작동하려면\n아래 권한이 필요합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        PermissionCard(
            icon = Icons.Outlined.Notifications,
            title = "알림 권한",
            description = "알람 알림을 표시하기 위해 필요합니다.",
            isGranted = notificationGranted,
            onRequestClick = {
                val activity = context as? android.app.Activity
                activity?.let {
                    PermissionManager.requestNotificationPermission(it, PermissionManager.REQUEST_CODE_NOTIFICATION)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PermissionCard(
            icon = Icons.Outlined.Alarm,
            title = "정확한 알람 권한",
            description = "정확한 시간에 알람이 울리도록 합니다.",
            isGranted = exactAlarmGranted,
            onRequestClick = {
                PermissionManager.openExactAlarmSettings(context)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PermissionCard(
            icon = Icons.Outlined.PhoneAndroid,
            title = "다른 앱 위에 표시",
            description = "잠금 화면에서 알람 화면을 띄웁니다.",
            isGranted = overlayGranted,
            onRequestClick = {
                PermissionManager.openOverlaySettings(context)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (notificationGranted && exactAlarmGranted && overlayGranted) {
            Button(
                onClick = onAllPermissionsGranted,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("시작하기")
            }
        } else {
            Text(
                text = "모든 권한을 허용해 주세요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequestClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isGranted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isGranted) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "허용됨",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                OutlinedButton(onClick = onRequestClick) {
                    Text("허용")
                }
            }
        }
    }
}
