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
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition

class NotificationActivity : AppCompatActivity() {
    var TAG = "[Notification Activity]"

    val CHANNEL_ID = "my-id"
    val textTitle = "My First Notif"
    val textContent = "Semoga aplikasinya lancar yaaaa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        onSlider()
        createNotification(com.example.lotus.models.Notification("Your Notification", "This is love only for you"))
    }

    private fun createNotification(data: com.example.lotus.models.Notification) {
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
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder)
        }

    }

    fun onSlider(){
        val config = SlidrConfig.Builder()
            .position(SlidrPosition.RIGHT)

        Slidr.attach(this, config.build())
    }

}