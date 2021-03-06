package com.example.lotus.utils

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService

fun likePost(postID: String, userID: String, token: String){
    AndroidNetworking.get(EnvService.ENV_API + "/posts/{postID}/likes/{userID}")
        .addHeaders("Authorization", "Bearer " + token)
        .addPathParameter("postID", postID)
        .addPathParameter("userID", userID)
        .setPriority(Priority.MEDIUM)
        .build()
        .getAsObject(
            Respon::class.java,
            object : ParsedRequestListener<Respon> {
                override fun onResponse(respon: Respon) {
                    if (respon.code.toString() == "200") {
                        Log.d("Like", "Success")
                    }else {
                        Log.e("ERROR!!!", "Like Post ${respon.code},\n${respon.data}, ${respon.data}")
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e("ERROR!!!", "Like Post ${anError.errorCode},\n${anError.errorDetail}")

                }
            })
}

fun dislikePost(postID: String, userID: String, token: String){
    AndroidNetworking.get(EnvService.ENV_API + "/posts/{postID}/dislike/{userID}")
        .addHeaders("Authorization", "Bearer " + token)
        .addPathParameter("postID", postID)
        .addPathParameter("userID", userID)
        .setPriority(Priority.MEDIUM)
        .build()
        .getAsObject(
            Respon::class.java,
            object : ParsedRequestListener<Respon> {
                override fun onResponse(respon: Respon) {
                    if (respon.code.toString() == "200") {
                        Log.d("Dislike", "Success")
                    }else {
                        Log.e("ERROR!!!", "Dislike Post ${respon.code}, ${respon.data}")
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e("ERROR!!!", "Dislike Post ${anError.errorCode}\n" +
                            "${anError.errorDetail}")

                }
            })
}