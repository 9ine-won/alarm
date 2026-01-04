package com.example.alarmgame.platform

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * 앱에 필요한 권한들을 관리하는 유틸리티 클래스입니다.
 */
object PermissionManager {
    /**
     * 알림 권한이 부여되었는지 확인합니다. (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * 정확한 알람 권한이 부여되었는지 확인합니다. (Android 12+)
     */
    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    /**
     * 다른 앱 위에 표시 권한이 부여되었는지 확인합니다. (Android 6+)
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    /**
     * 모든 필수 권한이 부여되었는지 확인합니다.
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return hasNotificationPermission(context) &&
            hasExactAlarmPermission(context) &&
            hasOverlayPermission(context)
    }

    /**
     * 알림 권한을 요청합니다. (Android 13+)
     */
    fun requestNotificationPermission(
        activity: Activity,
        requestCode: Int,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                requestCode,
            )
        }
    }

    /**
     * 정확한 알람 설정 화면을 엽니다. (Android 12+)
     */
    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent =
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            context.startActivity(intent)
        }
    }

    /**
     * 다른 앱 위에 표시 설정 화면을 엽니다. (Android 6+)
     */
    fun openOverlaySettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent =
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}"),
                )
            context.startActivity(intent)
        }
    }

    /**
     * 앱 상세 설정 화면을 엽니다.
     */
    fun openAppSettings(context: Context) {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        context.startActivity(intent)
    }

    const val REQUEST_CODE_NOTIFICATION = 1001
}
