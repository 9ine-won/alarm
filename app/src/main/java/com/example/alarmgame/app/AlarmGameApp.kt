package com.example.alarmgame.app

import android.app.Application
import com.example.alarmgame.platform.AlarmNotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlarmGameApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AlarmNotificationManager(this).createChannels()
    }
}
