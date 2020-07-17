package com.example.lotus.ui.notification

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.models.Notification
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.home.RecyclerViewLoadMoreScroll
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject


class NotificationActivity : AppCompatActivity() {
    var TAG = "[Notification Activity]"

    val CHANNEL_ID = "my-id"
    private val token = "5f02b3361718f5360aeff6d2"
    var notificationsData = ArrayList<Notification>()
    val username = "testaccount4"
    private val mSocket: Socket = IO.socket("http://34.101.109.136:3000")

    lateinit var adapter: NotificationAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var manager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val reloadNotification: PullRefreshLayout = findViewById(R.id.reloadNotification)
        val datanull = findViewById<LinearLayout>(R.id.dataNull)
        datanull.visibility = View.GONE

        mSocket.on("isi", onNewMessage)
        mSocket.connect()
        Log.d("SOCKET", "${mSocket.connected()},  ${mSocket.connect()}")
        getNotifications()
        onSlider()
        manager = getSupportFragmentManager()

        reloadNotification.setOnRefreshListener {
            getNotifications()
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        this.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            try {
                name = data.getString("name")
            } catch (e: JSONException) {
                return@Runnable
            }

            Log.d("name", name)

            createNotification("HELLO", name)

        })
    }

    fun loadNotification(data: ArrayList<Notification>, notification: RecyclerView){
        adapter = NotificationAdapter(data, this)
        adapter.notifyDataSetChanged()

        notification.adapter = adapter
        notification.setHasFixedSize(true)
        notification.layoutManager = LinearLayoutManager(this)
    }

    private fun getNotifications(){
        AndroidNetworking.get(EnvService.ENV_API + "/users/{userid}/notifications")
            .addPathParameter("userid", "5f0589e0f5cb8c4c1659c124")
            .addHeaders("Authorization", "Bearer " + token)
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Notification::class.java)
                                notificationsData.add(dataJson)
                            }

                            if (notificationsData.size < 1){
                                dataNull.visibility = View.VISIBLE
                            }else{
                                loadNotification(notificationsData, rvNotification)
                            }
                        }else {
                            Toast.makeText(this@NotificationActivity, "Error ${respon.code}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        reloadFeed.setRefreshing(false)
                        Toast.makeText(this@NotificationActivity, "Error ${anError.errorCode}", Toast.LENGTH_SHORT).show()
                        Log.d("Errornya disini kah?", anError.toString())
                    }
                })
    }

    private fun createNotification(title: String, content: String) {
        val id = 1
        val fullScreenIntent = Intent(this, NotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_lotus)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_lotus))
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(getResources().getColor(R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder)
        }

    }

    fun detailPost(postId: String) {
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
        appbarNotification.visibility = View.INVISIBLE
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.containerNotification, dataPost)
            ?.commit()
    }

    fun onSlider(){
        val config = SlidrConfig.Builder()
            .position(SlidrPosition.RIGHT)

        Slidr.attach(this, config.build())
    }

}