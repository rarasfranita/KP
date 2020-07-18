package com.example.lotus.ui.dm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.baoyz.widget.PullRefreshLayout
import com.example.lotus.R
import com.example.lotus.models.Dm
import com.example.lotus.models.Notification
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.home.RecyclerViewLoadMoreScroll
import com.example.lotus.ui.notification.NotificationAdapter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_homedm.*


class MainActivityDM : AppCompatActivity() {
    var TAG = "[DM Activity]"
    lateinit var mSocket: Socket;

    var dmData = ArrayList<Dm>()
    var username = SharedPrefManager.getInstance(this).user.username
    var userId = SharedPrefManager.getInstance(this).user._id
    var token = SharedPrefManager.getInstance(this).token.token

    lateinit var adapter: DmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            mSocket = IO.socket("http://34.101.109.136:3000")
            Log.d("success_Socket", mSocket.id())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }
        mSocket.connect()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maindm)
        val reload: PullRefreshLayout = findViewById(R.id.reloadDM)

        reload.setOnRefreshListener {
            getChanel()
        }
    }

    private fun getChanel() {
        AndroidNetworking.get(EnvService.ENV_API + "/users/{userid}/notifications")
            .addPathParameter("userid", "5f0466e2e524346040f53178")
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
                            Log.d("ResponDM", respon.data.toString())
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Dm::class.java)
                                dmData.add(dataJson)
                            }
                            loadDm(dmData, rv_listChat)
                        }else {
                            Toast.makeText(this@MainActivityDM, "Error ${respon.code}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        reloadFeed.setRefreshing(false)
                        Toast.makeText(this@MainActivityDM, "Error ${anError.errorCode}", Toast.LENGTH_SHORT).show()
                        Log.d("Errornya disini kah?", anError.toString())
                    }
                })
    }

    fun loadDm(data: ArrayList<Dm>, dm: RecyclerView){
        adapter = DmAdapter(data, this)
        adapter.notifyDataSetChanged()

        dm.adapter = adapter
        dm.setHasFixedSize(true)
        dm.layoutManager = LinearLayoutManager(this)
    }

}
