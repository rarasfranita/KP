package com.example.lotus.ui.explore.hashtag

import android.os.Bundle
import android.util.Log
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
import com.example.lotus.ui.explore.hashtag.model.Respons
import com.example.lotus.service.EnvService
import com.example.lotus.ui.detailpost.DetailPost
import com.example.lotus.ui.explore.hashtag.model.Data
import com.google.gson.Gson

class HashtagActivity : AppCompatActivity() {
    private var manager: FragmentManager? = null
    private val TAG = "HashtagActivity"
    private val token = "5f0a76f3ff07b60aaafd0090"
    var dataFeed = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_hashtag)

        AndroidNetworking.initialize(getApplicationContext());
        getHashtagMedia()
    }

    private fun getHashtagMedia() {
        AndroidNetworking.get(EnvService.ENV_API + "/feeds/explore/testing?username=testaccount1&index=0&type=media")
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
//                            for (res in respon.data) {
                            val res = respon.data
                            val strRes = gson.toJson(res)
                                val dataJson = gson.fromJson(strRes, Data::class.java)
                                dataFeed.add(dataJson)
//                            }
                            loadExploreHashtag(dataFeed, findViewById(R.id.rvExploreHashtag))

                        } else {
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("asuu nya media hashtag:", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })
    }

    fun loadExploreHashtag(data: ArrayList<Data>, hashtag: RecyclerView) {
        val adapter = HashtagMediaAdapter(data, this)
        adapter.notifyDataSetChanged()
        hashtag.adapter = adapter
        hashtag.setHasFixedSize(true)
        hashtag.layoutManager = LinearLayoutManager(this)
    }

    fun detailPost(data: Data) {
        val bundle = Bundle()
        bundle.putParcelable("data", data)
        val dataPost = DetailPost()
        dataPost.arguments = bundle
        manager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ?.replace(R.id.fragment_explore_hashtag, dataPost)
            ?.commit()

    }
//    fun loadExploreTextMore(data: ArrayList<Data>, explore: RecyclerView) {
//        explore.setHasFixedSize(true)
//        explore.layoutManager = LinearLayoutManager(this)
//        val adapter =
//            HashtagTextAdapter(data, this)
//        adapter.notifyDataSetChanged()
//
//        explore.adapter = adapter
//    }
}