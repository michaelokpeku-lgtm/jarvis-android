package com.michaelbtc.jarvisandroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class WakeWordService : Service() {

    companion object {
        private const val CHANNEL_ID = "jarvis_wake_word"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = Notification.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("JARVIS")
            .setContentText("JARVIS is ready")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        startForeground(
            NOTIFICATION_ID,
            notification
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        // Wake-word listening will be added here.
        // For now, this service simply stays alive
        // as a foreground service.

        return START_STICKY
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "JARVIS Wake Word",
            NotificationManager.IMPORTANCE_LOW
        )

        channel.description =
            "Keeps JARVIS ready for background voice features"

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
