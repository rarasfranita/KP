package com.example.lotus.ui.dm

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.DM.Channel.Dm
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.fragment_homedm.*


class MainActivityDM : AppCompatActivity() {

    var dmData = ArrayList<Dm>()
    var username = SharedPrefManager.getInstance(this).user.username
    var userId = SharedPrefManager.getInstance(this).user._id
    var token = SharedPrefManager.getInstance(this).token.token


    lateinit var adapter: DmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maindm)
        val datanull = findViewById<LinearLayout>(R.id.dataNull)
        datanull.visibility = View.GONE

        getListMessage()
    }

    private fun getListMessage() {
        AndroidNetworking.get(EnvService.ENV_API + "/users/{userid}/dm")
            .addPathParameter("userid", userId.toString())
            .addHeaders("Authorization", "Bearer " + token)
            .setTag(this)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Dm::class.java)
                                dmData.clear()
                                dmData.add(dataJson)
                            }


                            if (dmData.size < 1) {
                                dataNull.visibility = View.VISIBLE
                            } else {
                                loadDm(dmData, rv_listChat)
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivityDM,
                                "Error ${respon.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("Errornya disini kah?", anError.toString())
                    }
                })

    }

    fun loadDm(data: ArrayList<Dm>, notification: RecyclerView) {
        adapter = DmAdapter(data, this)
        adapter.notifyDataSetChanged()

        notification.adapter = adapter
        notification.setHasFixedSize(true)
        notification.layoutManager = LinearLayoutManager(this)
    }

}
