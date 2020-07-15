package com.example.lotus.ui.notification

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lotus.R
import com.example.lotus.models.Notification
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import org.json.JSONException
import org.json.JSONObject


class NotificationActivity : AppCompatActivity() {
    var TAG = "[Notification Activity]"

    val CHANNEL_ID = "my-id"
    val textTitle = "My First Notif"
    val textContent = "Semoga aplikasinya lancar yaaaa"

    private val mSocket: Socket = IO.socket("http://34.101.109.136:3000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        mSocket.on("isi", onNewMessage)
        mSocket.connect()
        Log.d("SOCKET", "${mSocket.connected()},  ${mSocket.connect()}")

        onSlider()
//        createNotification(com.example.lotus.models.Notification("Your Notification", "This is love only for you"))
    }

    private val onNewMessage = Emitter.Listener { args ->
        this.runOnUiThread(Runnable {
            Log.d("SINI", "SINI")
            val data = args[0] as JSONObject
            val name: String
//            val message: String
            try {
                name = data.getString("name")
//                message = data.getString("message")
            } catch (e: JSONException) {
                return@Runnable
            }

            Log.d("name", name)

            createNotification(Notification("HELLO", name))

        })
    }

    private fun createNotification(data: Notification) {
        val id = 1
        val fullScreenIntent = Intent(this, NotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_lotus)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lotus))
            .setContentTitle(data.title)
            .setContentText(data.content)
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