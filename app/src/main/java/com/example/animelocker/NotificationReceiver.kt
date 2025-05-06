package com.example.animelocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Anime Reminder"
        val content = intent.getStringExtra("content") ?: "Time to watch your anime!"

        val builder = NotificationCompat.Builder(context, "anime_channel")
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(title.hashCode(), builder.build())
            } else {
                // Optionally, log or handle lack of permission
                Log.w("NotificationReceiver", "Notification permission not granted")
            }
        }

    }
}
