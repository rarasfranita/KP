package com.example.lotus.service

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.models.Post
import com.example.lotus.models.Respons

class PostService {
    private val token = "5f02b3361718f5360aeff6d2"

    public fun getPostsHome(): MutableList<Post>?{
        var postData: MutableList<Post>? = null

        AndroidNetworking.get(EnvService.ENV_API + "/feeds/testaccount/-1")
            .addHeaders("Authorization", "Bearer " + token)
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(
                Respons::class.java,
                object : ParsedRequestListener<Respons> {
                    override fun onResponse(respon: Respons) {
                        // do anything with response
//                        Log.d("Hello sayang", respon.code.toString())
//                        Log.d("Hello sayang2", respon.data.toString())

                        if (respon.code == 200) {

                            postData = respon.data as MutableList<Post>
                            Log.d("HAlo sayang lagi", postData.toString())
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("Error hud", anError.toString())

                    }
                })

        Log.d("Return data huda", postData.toString())
        return postData
    }
}