package com.example.lotus.service

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.models.Post
import com.example.lotus.models.Respons
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class PostService {
    private val token = "5f058e0cf5cb8c4c1659c12d"

    suspend fun getPostsHome(): ArrayList<Post>{
        var dataFeed = ArrayList<Post>()

        AndroidNetworking.get(EnvService.ENV_API + "/feeds/testaccount1/-1")
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
                                val dataJson = gson.fromJson(strRes, Post::class.java)
                                dataFeed.add(dataJson)
                            }

                        }else {
//                            TODO: Create error page and show what the error
//                            Toast.makeText(coroutineContext(), "Error ${respon.code}", Toast.LENGTH_SHORT)
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.d("Errornya disini kah?", anError.toString())
                        // Next go to error page (Popup error)
                    }
                })

        delay(1000)
        if (dataFeed != null){
            return dataFeed
        }

        throw CancellationException("Can't get data")
    }

    suspend fun like(){
        // TODO connect BE to like
    }

    suspend fun dislike(){
        // TODO connect BE to dislike
    }

    suspend fun sendComment(){
        // TODO send comment to BE
    }

    suspend fun getComment(){
        // TODO get comment from BE
    }
}