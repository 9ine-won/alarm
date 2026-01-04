package com.example.alarmgame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.alarmgame.platform.PermissionManager
import com.example.alarmgame.ui.screen.edit.AlarmEditScreen
import com.example.alarmgame.ui.screen.list.AlarmListScreen
import com.example.alarmgame.ui.screen.permission.PermissionScreen
import com.example.alarmgame.ui.screen.settings.SettingsScreen

object Destinations {
    const val PERMISSION = "permission"
    const val ALARM_LIST = "alarm/list"
    const val ALARM_EDIT = "alarm/edit"
    const val SETTINGS = "settings"
}

const val ARG_ALARM_ID = "alarmId"
private const val ALARM_EDIT_ROUTE = "${Destinations.ALARM_EDIT}?$ARG_ALARM_ID={$ARG_ALARM_ID}"

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    
    // 모든 권한이 있으면 바로 알람 목록으로, 없으면 권한 화면으로 시작
    val startDestination = if (PermissionManager.hasAllRequiredPermissions(context)) {
        Destinations.ALARM_LIST
    } else {
        Destinations.PERMISSION
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.PERMISSION) {
            PermissionScreen(
                onAllPermissionsGranted = {
                    navController.navigate(Destinations.ALARM_LIST) {
                        popUpTo(Destinations.PERMISSION) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.ALARM_LIST) {
            AlarmListScreen(
                onAddAlarm = { navController.navigate("${Destinations.ALARM_EDIT}?$ARG_ALARM_ID=-1") },
                onOpenSettings = { navController.navigate(Destinations.SETTINGS) },
                onEditAlarm = { alarmId ->
                    navController.navigate("${Destinations.ALARM_EDIT}?$ARG_ALARM_ID=$alarmId")
                }
            )
        }
        composable(
            route = ALARM_EDIT_ROUTE,
            arguments = listOf(
                navArgument(ARG_ALARM_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getLong(ARG_ALARM_ID) ?: -1L
            AlarmEditScreen(
                alarmId = alarmId.takeIf { it > 0 },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
