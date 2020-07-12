package com.example.lotus.ui.explore.general

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.general.model.Data
import com.example.lotus.ui.explore.hashtag.ListMediaHashtag
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_home.*

class GeneralActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    private val TAG = "ExploreActivity"
    private val token = "5f09a143ff07b60aaafd008d"
    var dataFeed = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_explore)

        AndroidNetworking.initialize(getApplicationContext());
        getExploreMedia()
        getExploreText()
    }


    private fun getExploreMedia() {
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?username=testaccount&index=0&type=media")
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
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataFeed.add(dataJson)
                            }
                            loadExploreMedia(dataFeed, findViewById(R.id.rvExploreMedia))

                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("asuu nya media explore : ", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }

    private fun getExploreText() {
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore?username=testaccount&index=0&type=text")
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
                            for (res in respon.data) {
                                val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataFeed.add(dataJson)
                            }
                            loadExploreText(dataFeed, findViewById(R.id.rvExploreText))

                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("asuu nya text explore :", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }


    fun loadExploreText(data: ArrayList<Data>, explore: RecyclerView) {
        explore.setHasFixedSize(true)
        explore.layoutManager = LinearLayoutManager(this)
        val adapter =
            GeneralTextAdapter(data, this)
        adapter.notifyDataSetChanged()

        explore.adapter = adapter
    }

    fun loadExploreMedia(data: ArrayList<Data>, explore: RecyclerView) {
        explore.setHasFixedSize(true)
        explore.layoutManager = LinearLayoutManager(this)
        val adapter =
            GeneralMediaAdapter(data, this)
        adapter.notifyDataSetChanged()

        explore.adapter = adapter
    }

    //tambahan, coba intent ke detail post
    fun detailExplore(item: Data) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        appBarLayout?.setVisibility(View.INVISIBLE)
        bottom_sheet?.setVisibility(View.INVISIBLE)
        fab_post?.setVisibility(View.INVISIBLE)
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragmentHome, dataPost)
            ?.commit()
    }
    //tambahan, coba intent ke hashtag
    fun more(item : Data) {
        val bundle = Bundle()
        bundle.putParcelable("data", item)
        val dataPost = ListMediaHashtag()
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.hashtagFragment, dataPost)
            ?.commit()
    }
}