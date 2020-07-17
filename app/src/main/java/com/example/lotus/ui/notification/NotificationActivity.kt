package com.example.lotus.ui.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.home.RecyclerViewLoadMoreScroll
import com.example.lotus.ui.profile.ProfileActivity
import com.google.gson.Gson
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.activity_notification.*


class NotificationActivity : AppCompatActivity() {
    var TAG = "[Notification Activity]"

    var token = SharedPrefManager.getInstance(this).token.token
    var notificationsData = ArrayList<Notification>()
    var userID = SharedPrefManager.getInstance(this).user._id

    lateinit var adapter: NotificationAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var manager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val reloadNotification: PullRefreshLayout = findViewById(R.id.reloadNotification)
        val datanull = findViewById<LinearLayout>(R.id.noData)
        datanull.visibility = View.GONE

        getNotifications(null)
        onSlider()
        manager = getSupportFragmentManager()

        reloadNotification.setOnRefreshListener {
            getNotifications(reloadNotification)
        }
        listenAppToolbar()
    }

    fun loadNotification(data: ArrayList<Notification>, notification: RecyclerView){
        adapter = NotificationAdapter(data, this)
        adapter.notifyDataSetChanged()

        notification.adapter = adapter
        notification.setHasFixedSize(true)
        notification.layoutManager = LinearLayoutManager(this)
    }

    private fun getNotifications(v: PullRefreshLayout?){
        if (v !=  null){
            v.setRefreshing(true)
        }

        AndroidNetworking.get(EnvService.ENV_API + "/users/{userid}/notifications")
            .addPathParameter("userid", userID.toString())
            .addHeaders("Authorization", "Bearer " + token)
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        reloadNotification.setRefreshing(false)
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            for ((i, res) in respon.data.withIndex()) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Notification::class.java)
                                notificationsData.clear()
                                notificationsData.add(dataJson)
                            }

                            if (notificationsData.size < 1){
                                noData.visibility = View.VISIBLE
                            }else{
                                loadNotification(notificationsData, rvNotification)
                            }
                        }else {
                            Toast.makeText(this@NotificationActivity, "Error ${respon.code}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        reloadNotification.setRefreshing(false)
                        Toast.makeText(this@NotificationActivity, "Error ${anError.errorCode}", Toast.LENGTH_SHORT).show()
                        Log.d("Errornya disini kah?", anError.toString())
                    }
                })
    }

    fun gotoProfilePicture(username: String){
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
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

    private fun listenAppToolbar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbarNotification)

        toolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }

    }

}