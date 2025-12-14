package com.example.alarmgame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.alarmgame.ui.screen.edit.AlarmEditScreen
import com.example.alarmgame.ui.screen.list.AlarmListScreen
import com.example.alarmgame.ui.screen.settings.SettingsScreen

object Destinations {
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
    NavHost(
        navController = navController,
        startDestination = Destinations.ALARM_LIST,
        modifier = modifier
    ) {
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
