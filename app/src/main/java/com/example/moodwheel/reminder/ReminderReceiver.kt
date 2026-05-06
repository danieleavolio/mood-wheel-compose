package com.example.moodwheel.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moodwheel.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.setup(context)
            return
        }

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

        val hasEntryToday = runBlocking {
            AppDatabase.getInstance(context).entryDao().countBetween(start, end) > 0
        }

        if (!hasEntryToday) {
            ReminderScheduler.showReminder(context)
        }
    }
}
