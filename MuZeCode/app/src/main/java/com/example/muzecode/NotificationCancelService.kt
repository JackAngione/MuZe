package com.example.muzecode

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationCancelService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val notificationId = 0xb339
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)


        stopSelf()
    }
}