package com.example.lotus.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lotus.R
import com.r0adkll.slidr.Slidr

class NotificationActivity : AppCompatActivity() {
    var TAG = "[Notification Activity]"

    val CHANNEL_ID = "my-id"
    val textTitle = "My First Notif"
    val textContent = "Semoga aplikasinya lancar yaaaa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        Slidr.attach(this)

        createNotification()
    }

    private fun createNotification() {
        val id = 1
        val fullScreenIntent = Intent(this, NotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_lotus)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lotus))
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(getResources().getColor(R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(fullScreenPendingIntent, true)


        with(NotificationManagerCompat.from(this)) {
            notify(id, builder.build())
        }
    }

}