package com.example.alarmgame.domain.scheduler

import com.example.alarmgame.domain.model.Alarm

interface AlarmScheduler {
    /**
     * Schedule or reschedule an alarm according to nextTriggerAt.
     */
    fun schedule(alarm: Alarm)

    /**
     * Cancel an alarm for the given id.
     */
    fun cancel(alarmId: Long)

    /**
     * Reschedule all alarms after reboot or permission change.
     */
    fun rescheduleAll(alarms: List<Alarm>)
}
